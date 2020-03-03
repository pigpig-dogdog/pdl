package cn.lj.pdl.scheduler;

import cn.lj.pdl.constant.StorageConstants;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.mapper.AlgoTrainMapper;
import cn.lj.pdl.model.AlgoTrainDO;
import cn.lj.pdl.service.K8sService;
import cn.lj.pdl.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author luojian
 * @date 2019/12/17
 */
@Component
@Slf4j
public class AlgoTrainScheduler {

    private AlgoTrainMapper algoTrainMapper;
    private K8sService k8sService;
    private StorageService storageService;

    @Autowired
    public AlgoTrainScheduler(AlgoTrainMapper algoTrainMapper,
                              K8sService k8sService,
                              StorageService storageService) {
        this.algoTrainMapper = algoTrainMapper;
        this.k8sService = k8sService;
        this.storageService = storageService;
    }

    @Scheduled(fixedDelay = 5 * 1000)
    public void refreshAlgoTrainStatus() {
        // 获取所有状态为 RUNNING 的 AlgoTrainDO
        TrainStatus oldStatus = TrainStatus.RUNNING;
        List<AlgoTrainDO> list = algoTrainMapper.findByStatus(oldStatus);

        for (AlgoTrainDO algoTrainDO : list) {
            // 调用k8s服务，查询最新状态
            TrainStatus newStatus = k8sService.getJobStatus(algoTrainDO.getUuid());
            // 如果状态发生变化，更新状态
            if (!newStatus.equals(oldStatus)) {
                // 若 k8s job 运行结束，获取job运行日志，上传至文件服务器，并删除 k8s job
                if (newStatus.equals(TrainStatus.SUCCESS) || newStatus.equals(TrainStatus.FAILED)) {
                    String log = k8sService.getJobLog(algoTrainDO.getUuid());
                    storageService.write(StorageConstants.getAlgoTrainLogPath(algoTrainDO.getUuid()), log);
                    // todo
//                    k8sService.deleteJob(algoTrainDO.getUuid());
                }

                algoTrainMapper.updateStatus(algoTrainDO.getId(), newStatus);
                log.info("更新AlgoTrain状态, id:{}, name:{}, oldStatus:{}, newStatus:{}.", algoTrainDO.getId(), algoTrainDO.getName(), oldStatus, newStatus);
            }
        }
    }
}
