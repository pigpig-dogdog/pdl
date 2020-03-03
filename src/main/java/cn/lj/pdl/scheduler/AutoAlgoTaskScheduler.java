package cn.lj.pdl.scheduler;

import cn.lj.pdl.constant.*;
import cn.lj.pdl.dto.algodeploy.AlgoDeployCreateRequest;
import cn.lj.pdl.mapper.AlgoTrainMapper;
import cn.lj.pdl.mapper.AutoAlgoTaskMapper;
import cn.lj.pdl.model.AlgoTrainDO;
import cn.lj.pdl.model.AutoAlgoTaskDO;
import cn.lj.pdl.service.AlgoDeployService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luojian
 * @date 2019/12/25
 */
@Component
@Slf4j
public class AutoAlgoTaskScheduler {
    private AlgoTrainMapper algoTrainMapper;
    private AutoAlgoTaskMapper autoAlgoTaskMapper;
    private AlgoDeployService algoDeployService;
    private StorageService storageService;

    @Autowired
    public AutoAlgoTaskScheduler(AlgoTrainMapper algoTrainMapper,
                                 AutoAlgoTaskMapper autoAlgoTaskMapper,
                                 AlgoDeployService algoDeployService,
                                 StorageService storageService) {
        this.algoTrainMapper = algoTrainMapper;
        this.autoAlgoTaskMapper = autoAlgoTaskMapper;
        this.algoDeployService = algoDeployService;
        this.storageService = storageService;
    }

    @Scheduled(fixedDelay = 10 * 1000)
    public void refreshAutoAlgoTaskStatus() {
        List<AutoAlgoTaskDO> list = autoAlgoTaskMapper.findByStatus(AutoAlgoTaskStatus.RUNNING);

        for (AutoAlgoTaskDO autoAlgoTaskDO : list) {
            try {
                handle(autoAlgoTaskDO);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void handle(AutoAlgoTaskDO autoAlgoTaskDO) {
        log.info("handle auto algo task, id: {}", autoAlgoTaskDO.getId());
        int running = 0;
        int success = 0;
        int failed = 0;

        List<AlgoTrainDO> successList = new ArrayList<>();
        for (String algoTrainId : autoAlgoTaskDO.getAlgoTrainIdList().split("\\s+")) {
            AlgoTrainDO algoTrainDO = algoTrainMapper.findById(Long.parseLong(algoTrainId));
            switch (algoTrainDO.getStatus()) {
                case RUNNING:
                    running++; break;
                case SUCCESS:
                    success++; successList.add(algoTrainDO); break;
                case FAILED:
                    failed++; break;
                default:
                    throw new RuntimeException("Unknown AlgoTrain Status.");
            }
        }

        if (running != 0) {
            // 还有训练任务还没结束
            log.info("still in training");
            return;
        }

        int total = running + success + failed;
        if (failed == total) {
            // 训练任务全部失败
            log.error("all algo train failed! auto algo task failed!");
            autoAlgoTaskMapper.updateStatus(autoAlgoTaskDO.getId(), AutoAlgoTaskStatus.FAILED);
            return;
        }

        if (success == 0) {
            // 训练任务全部失败
            log.error("all algo train failed! auto algo task failed!");
            autoAlgoTaskMapper.updateStatus(autoAlgoTaskDO.getId(), AutoAlgoTaskStatus.FAILED);
            return;
        }

        // 在成功训练的任务列表中，选择准确率最高的那个任务进行部署
        AlgoTrainDO bestAlgoTrainDO = null;
        double bestAcc = 0.0;
        for (AlgoTrainDO algoTrainDO : successList) {
            String accStr;
            try {
                accStr = storageService.read(StorageConstants.getAutoAlgoTaskAlgoTrainAccuracy(autoAlgoTaskDO.getUuid(), algoTrainDO.getUuid()));
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
                continue;
            }
            double acc = Double.parseDouble(accStr);
            if (acc > bestAcc) {
                bestAcc = acc;
                bestAlgoTrainDO = algoTrainDO;
            }
        }

        if (bestAlgoTrainDO == null) {
            return;
        }

        log.info("best test acc from algo train id: {}", bestAlgoTrainDO.getId());
        log.info("deploy the best model");

        // 部署
        AlgoDeployCreateRequest request = new AlgoDeployCreateRequest();
        request.setName(String.format("数据集(%s)_自助式算法任务_在线化服务", autoAlgoTaskDO.getDatasetName()));
        request.setMainClassPath(Constants.AUTO_ALGO_TASK_ALGODEPLOY_MAIN_CLASS_PATH);
        request.setReplicas(1);
        if (AlgoType.CLASSIFICATION.equals(autoAlgoTaskDO.getAlgoType())) {
            request.setLanguage(bestAlgoTrainDO.getLanguage());
            request.setFramework(bestAlgoTrainDO.getFramework());
        } else {
            request.setLanguage(Language.PYTHON_3_6);
            request.setFramework(Framework.TENSORFLOW_2_0_0);
        }

        String algoDeployUuid = CommonUtil.generateUuidStartWithAlphabet();
        String srcCodeZipFilePath = StorageConstants.getAutoAlgoTaskAlgoDeployCodeZipFilePath(autoAlgoTaskDO.getUuid(), bestAlgoTrainDO.getUuid());
        String filename = FilenameUtils.getName(srcCodeZipFilePath);
        String dstCodeZipFilePath = StorageConstants.getAlgoDeployCodeZipFilePath(algoDeployUuid, filename);
        storageService.copy(srcCodeZipFilePath, dstCodeZipFilePath);
        Long algoDeployId = algoDeployService.create(request, dstCodeZipFilePath, algoDeployUuid, bestAlgoTrainDO.getCreatorName());
        log.info("deploy done. algo deploy id: {}", algoDeployId);
        autoAlgoTaskMapper.updateAlgoDeployId(autoAlgoTaskDO.getId(), algoDeployId);
        autoAlgoTaskMapper.updateStatus(autoAlgoTaskDO.getId(), AutoAlgoTaskStatus.SUCCESS);
    }
}
