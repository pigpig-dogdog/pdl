package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.constant.K8sConstants;
import cn.lj.pdl.constant.StorageConstants;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algotrain.AlgoTrainCreateRequest;
import cn.lj.pdl.mapper.AlgoTrainMapper;
import cn.lj.pdl.model.AlgoTrainDO;
import cn.lj.pdl.service.AlgoTrainService;
import cn.lj.pdl.service.K8sService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luojian
 * @date 2019/11/30
 */
@Service
public class AlgoTrainServiceImpl implements AlgoTrainService {

    private AlgoTrainMapper algoTrainMapper;
    private StorageService storageService;
    private K8sService k8sService;

    @Autowired
    public AlgoTrainServiceImpl(AlgoTrainMapper algoTrainMapper,
                                StorageService storageService,
                                K8sService k8sService) {
        this.algoTrainMapper = algoTrainMapper;
        this.storageService = storageService;
        this.k8sService = k8sService;
    }

    @Override
    public void create(AlgoTrainCreateRequest request, MultipartFile codeZipFile, String requestUsername) throws IOException {

        String uuid = CommonUtil.generateUuidStartWithAlphabet();

        // 文件服务 创建目录
        storageService.createDirs(
                StorageConstants.getAlgoTrainRootPath(),
                StorageConstants.getAlgoTrainDirPath(uuid)
        );

        // 文件服务 上传代码文件
        String codeZipFilePath = StorageConstants.getAlgoTrainCodeZipFilePath(uuid, codeZipFile.getOriginalFilename());
        storageService.uploadFile(codeZipFilePath, codeZipFile.getBytes());

        // k8s服务
        List<String> args = new ArrayList<String>() {{
            add(K8sConstants.ALGO_TRAIN_ENTRY);
            add("--user_code_oss_path=" + codeZipFilePath);
            add("--entry_and_arg=" + request.getEntryAndArgs());
            add("--result_dir_path=" + request.getResultDirPath());
            add("--result_oss_path=" + StorageConstants.getAlgoTrainResultZipFilePath(uuid));
        }};
        k8sService.createJob(
                uuid,
                K8sConstants.ALGO_TRAIN_IMAGE,
                K8sConstants.ALGO_TRAIN_COMMAND,
                args,
                CommonUtil.encodeChinese(requestUsername),
                CommonUtil.encodeChinese(request.getName())
        );

        AlgoTrainDO algoTrainDO = new AlgoTrainDO();
        algoTrainDO.setCreatorName(requestUsername);
        algoTrainDO.setName(request.getName());
        algoTrainDO.setFramework(request.getFramework());
        algoTrainDO.setEntryAndArgs(request.getEntryAndArgs());
        algoTrainDO.setResultDirPath(request.getResultDirPath());
        algoTrainDO.setUuid(uuid);
        algoTrainDO.setStatus(TrainStatus.RUNNING);
        algoTrainDO.setCodeZipFilePath(codeZipFilePath);
        algoTrainDO.setResultZipFileUrl(null);
        algoTrainMapper.insert(algoTrainDO);
    }

    @Override
    public Long create(AlgoTrainCreateRequest request, String codeZipFilePath, String algoTrainUuid, String requestUsername) {

        String uuid = (algoTrainUuid != null) ? algoTrainUuid : CommonUtil.generateUuidStartWithAlphabet();

        // 文件服务 创建目录
        storageService.createDirs(
                StorageConstants.getAlgoTrainRootPath(),
                StorageConstants.getAlgoTrainDirPath(uuid)
        );

        // k8s服务
        List<String> args = new ArrayList<String>() {{
            add(K8sConstants.ALGO_TRAIN_ENTRY);
            add("--user_code_oss_path=" + codeZipFilePath);
            add("--entry_and_arg=" + request.getEntryAndArgs());
            add("--result_dir_path=" + request.getResultDirPath());
            add("--result_oss_path=" + StorageConstants.getAlgoTrainResultZipFilePath(uuid));
        }};
        k8sService.createJob(
                uuid,
                K8sConstants.ALGO_TRAIN_IMAGE,
                K8sConstants.ALGO_TRAIN_COMMAND,
                args,
                CommonUtil.encodeChinese(requestUsername),
                CommonUtil.encodeChinese(request.getName())
        );

        AlgoTrainDO algoTrainDO = new AlgoTrainDO();
        algoTrainDO.setCreatorName(requestUsername);
        algoTrainDO.setName(request.getName());
        algoTrainDO.setFramework(request.getFramework());
        algoTrainDO.setEntryAndArgs(request.getEntryAndArgs());
        algoTrainDO.setResultDirPath(request.getResultDirPath());
        algoTrainDO.setUuid(uuid);
        algoTrainDO.setStatus(TrainStatus.RUNNING);
        algoTrainDO.setCodeZipFilePath(codeZipFilePath);
        algoTrainDO.setResultZipFileUrl(null);
        return algoTrainMapper.insert(algoTrainDO);
    }

    @Override
    public PageResponse<AlgoTrainDO> list(Integer pageNumber, Integer pageSize,
                                          String creatorName, String name, Framework framework, TrainStatus status) {
        // 分页信息
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize);

        // 条件查询信息
        AlgoTrainDO condition = new AlgoTrainDO();
        condition.setCreatorName(creatorName);
        condition.setName(name);
        condition.setFramework(framework);
        condition.setStatus(status);

        // 统计符合条件的数据行数
        Integer totalItemsNumber = algoTrainMapper.countByCondition(condition);

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageSize));

        // 获取符合条件的数据，如果查询的页面超出了数据条数范围，那就无需再查数据库
        List<AlgoTrainDO> list = pageInfo.getStartIndex() >= totalItemsNumber
                ? new ArrayList<>()
                : algoTrainMapper.findByCondition(condition, pageInfo);

        return new PageResponse<>(pageNumber, pageSize, totalItemsNumber, totalPagesNumber, list);
    }

    @Override
    public String getLog(Long id) throws IOException {
        AlgoTrainDO algoTrainDO = algoTrainMapper.findById(id);
        if (TrainStatus.RUNNING.equals(algoTrainDO.getStatus())) {
            // 正在运行的任务从k8s接口读取日志
            return k8sService.getJobLog(algoTrainDO.getUuid());
        } else {
            // 运行结束的任务从文件服务接口读取日志
            String log = storageService.read(StorageConstants.getAlgoTrainLogPath(algoTrainDO.getUuid()));
            return log == null ? "日志未上传" : log;
        }
    }

}
