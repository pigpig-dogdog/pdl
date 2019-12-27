package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.constant.StorageConstants;
import cn.lj.pdl.dto.autoalgotask.AutoAlgoTaskDetailResponse;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.mapper.*;
import cn.lj.pdl.model.*;
import cn.lj.pdl.service.AlgoTrainService;
import cn.lj.pdl.service.AutoAlgoTaskService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author luojian
 * @date 2019/12/24
 */
@Slf4j
@Service
public class AutoAlgoTaskServiceImpl implements AutoAlgoTaskService {

    private DatasetMapper datasetMapper;
    private ImageMapper imageMapper;
    private AlgoTrainMapper algoTrainMapper;
    private AlgoDeployMapper algoDeployMapper;
    private AutoAlgoTaskMapper autoAlgoTaskMapper;
    private AlgoTrainService algoTrainService;
    private StorageService storageService;

    @Autowired
    public AutoAlgoTaskServiceImpl(DatasetMapper datasetMapper,
                                   ImageMapper imageMapper,
                                   AlgoTrainMapper algoTrainMapper,
                                   AlgoDeployMapper algoDeployMapper,
                                   AutoAlgoTaskMapper autoAlgoTaskMapper,
                                   AlgoTrainService algoTrainService,
                                   StorageService storageService) {
        this.datasetMapper = datasetMapper;
        this.imageMapper = imageMapper;
        this.algoTrainMapper = algoTrainMapper;
        this.algoDeployMapper = algoDeployMapper;
        this.autoAlgoTaskMapper = autoAlgoTaskMapper;
        this.algoTrainService = algoTrainService;
        this.storageService = storageService;
    }

    @Override
    public void create(Long datasetId, String requestUsername) {

        DatasetDO datasetDO = datasetMapper.findById(datasetId);

        // 数据集不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        // 统计数据集已标注的图片数
        ImageDO condition = new ImageDO();
        condition.setDatasetId(datasetId);
        condition.setAnnotated(true);
        Integer annotatedImagesNumber = imageMapper.countByCondition(condition);

        // 生成训练集与测试集
        Pair<List<String>, List<String>> p = getTrainListAndTestList(datasetDO.getUuid(),
                Constants.AUTO_ALGO_TASK_DATASET_TRAIN_RATIO,
                Constants.AUTO_ALGO_TASK_DATASET_TEST_RATIO);
        List<String> trainList = p.getLeft();
        List<String> testList = p.getRight();

        log.info("split annotated images({}) to trainSet({}) testSet({})", annotatedImagesNumber, trainList.size(), testList.size());

        // 生成自助式算法任务的uuid
        String uuid = CommonUtil.generateUuid();

        // 文件服务
        storageService.createDirs(
                StorageConstants.getAutoAlgoTaskRootPath(),
                StorageConstants.getAutoAlgoTaskDirPath(uuid)
        );
        String trainListPath = StorageConstants.getAutoAlgoTaskDatasetTrainList(uuid);
        String testListPath = StorageConstants.getAutoAlgoTaskDatasetTestList(uuid);
        storageService.write(trainListPath, String.join("\n", trainList));
        storageService.write(testListPath, String.join("\n", testList));

//        // 创建多个训练任务
//        String[] models = (datasetDO.getAlgoType() == AlgoType.CLASSIFICATION) ? Constants.CLASSIFICATION_MODELS : Constants.DETECTION_MODELS;
//        List<Long> algoTrainIdList = new ArrayList<>();
//        for (String model : models) {
//            String algoTrainUuid = CommonUtil.generateUuidStartWithAlphabet();
//            String accuracyPath = StorageConstants.getAutoAlgoTaskAlgoTrainAccuracy(uuid, algoTrainUuid);
//            String algoDeployCodeZipFilePath = StorageConstants.getAutoAlgoTaskAlgoDeployCodeZipFilePath(uuid, algoTrainUuid);
//
//
//            AlgoTrainCreateRequest request = new AlgoTrainCreateRequest();
//            // todo
//            request.setName("todo");
//            request.setFramework(Constants.AUTO_ALGO_TASK_ALGOTRAIN_FRAMEWORK);
//            request.setEntryAndArgs(String.format(
//                    "python main.py --classes_names_str=%s --train_list_path=%s --test_list_path=%s --algo_type=%s --model_name=%s --accuracy_oss_path=%s --algo_deploy_code_zip_file_oss_path=%s",
//                    classesNames, trainListPath, testListPath, algoType, model, accuracyPath, algoDeployCodeZipFilePath
//            ));
//            request.setResultDirPath("todo");
//
//            Long algoTrainId = algoTrainService.create(
//                    request,
//                    StorageConstants.getAutoAlgoTaskAlgoTrainCodeZipFilePath(),
//                    algoTrainUuid,
//                    Constants.AUTO_ALGO_TASK_ALGOTRAIN_CREATOR_NAME
//            );
//            algoTrainIdList.add(algoTrainId);
//        }
//
//        // 数据库
//        AutoAlgoTaskDO autoAlgoTaskDO = new AutoAlgoTaskDO();
//        autoAlgoTaskDO.setCreatorName(requestUsername);
//        // todo
//        autoAlgoTaskDO.setName("todo");
//        autoAlgoTaskDO.setUuid(uuid);
//        autoAlgoTaskDO.setAlgoType(datasetDO.getAlgoType());
//        autoAlgoTaskDO.setDatasetId(datasetId);
//        autoAlgoTaskDO.setAlgoTrainIdList(StringUtils.join(algoTrainIdList, " "));
//        autoAlgoTaskDO.setAlgoDeployId(null);
//        autoAlgoTaskDO.setStatus(AutoAlgoTaskStatus.RUNNING);
//        autoAlgoTaskMapper.insert(autoAlgoTaskDO);
    }

    @Override
    public AutoAlgoTaskDetailResponse detail(Long autoAlgoTaskId) {
        // 获取自助式算法任务详情
        AutoAlgoTaskDO autoAlgoTaskDO = autoAlgoTaskMapper.findById(autoAlgoTaskId);
        if (autoAlgoTaskDO == null) {
            throw new BizException(BizExceptionEnum.AUTO_ALGO_TASK_NOT_EXIST);
        }

        // 获取训练详情
        List<AlgoTrainDO> algoTrainList = new ArrayList<>();
        if (autoAlgoTaskDO.getAlgoTrainIdList() != null) {
            for (String algoTrainId : autoAlgoTaskDO.getAlgoTrainIdList().split(" ")) {
                AlgoTrainDO algoTrainDO = algoTrainMapper.findById(Long.parseLong(algoTrainId));
                algoTrainList.add(algoTrainDO);
            }
        }

        // 获取部署详情
        AlgoDeployDO algoDeployDO = algoDeployMapper.findById(autoAlgoTaskDO.getAlgoDeployId());

        // 返回结果
        AutoAlgoTaskDetailResponse response = new AutoAlgoTaskDetailResponse();
        response.setAutoAlgoTask(autoAlgoTaskDO);
        response.setAlgoTrainList(algoTrainList);
        response.setAlgoDeploy(algoDeployDO);
        return response;
    }

    private Pair<List<String>, List<String>> getTrainListAndTestList(String datasetUuid, double trainRatio, double testRatio) {
        if ((trainRatio + testRatio) != 1.0) {
            throw new BizException(BizExceptionEnum.AUTO_ALGO_TASK_TRAIN_TEST_RATIO_SUM_NOT_EQUAL_TO_ONE);
        }

        // 遍历annotation目录，此目录下的所有图片都已经标注
        String datasetAnnotationsDir = StorageConstants.getDatasetAnnotationsDirPath(datasetUuid);
        List<String> annotationPaths = storageService.listFiles(datasetAnnotationsDir);

        // 生成数据集的文本行以供python训练程序下载数据, 每行格式: imagePath annotationPath
        List<String> datasetList = new ArrayList<>();
        for (String annotationPath : annotationPaths) {
            // imagePath      example: "datasets/datasetUuid/images/imageFilename"
            // annotationPath example: "datasets/datasetUuid/annotations/imageFilename.xml"
            String imageFilename = FilenameUtils.getBaseName(annotationPath);
            String imagePath = StorageConstants.getDatasetImagePath(datasetUuid, imageFilename);
            String line = imagePath + " " + annotationPath;
            datasetList.add(line);
        }

        // 随机打乱
        Collections.shuffle(datasetList);

        int trainSize = (int)(datasetList.size() * trainRatio);
        int testSize = datasetList.size() - trainSize;
        List<String> trainList = new ArrayList<>(trainSize);
        List<String> testList = new ArrayList<>(testSize);

        for (String line : datasetList) {
            if (trainList.size() < trainSize) {
                trainList.add(line);
            } else {
                testList.add(line);
            }
        }

        return new ImmutablePair<>(trainList, testList);
    }
}
