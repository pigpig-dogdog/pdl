package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.*;
import cn.lj.pdl.dto.algotrain.AlgoTrainCreateRequest;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

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

        log.info("create auto algo task, dataset id: {}, name: {}", datasetDO.getId(), datasetDO.getName());

        // 统计数据集已标注的图片数
        ImageDO condition = new ImageDO();
        condition.setDatasetId(datasetId);
        condition.setAnnotated(true);
        Integer annotatedImagesNumber = imageMapper.countByCondition(condition);

        // 已标注的图片数量过少，不建议开启自助式算法任务
        if (annotatedImagesNumber < Constants.AUTO_ALGO_TASK_ALGOTRAIN_MIN_ANNOTATED_IMAGES_NUMBER) {
            throw new BizException(BizExceptionEnum.AUTO_ALGO_TASK_DATASET_ANNOTATED_NUMBER_TOO_LESS);
        }

        // 生成训练集与测试集
        Pair<List<String>, List<String>> p = getTrainListAndTestList(datasetDO.getUuid(),
                Constants.AUTO_ALGO_TASK_DATASET_TRAIN_RATIO,
                Constants.AUTO_ALGO_TASK_DATASET_TEST_RATIO);
        List<String> trainList = p.getLeft();
        List<String> testList = p.getRight();

        log.info("split annotated images({}) to trainSet({}) testSet({})", annotatedImagesNumber, trainList.size(), testList.size());

        // 生成自助式算法任务的uuid
        String uuid = CommonUtil.generateUuid();
        System.out.println(uuid);

        // 文件服务
        storageService.createDirs(
                StorageConstants.getAutoAlgoTaskRootPath(),
                StorageConstants.getAutoAlgoTaskDirPath(uuid)
        );
        String trainListPath = StorageConstants.getAutoAlgoTaskDatasetTrainList(uuid);
        String testListPath = StorageConstants.getAutoAlgoTaskDatasetTestList(uuid);
        storageService.write(trainListPath, String.join("\n", trainList));
        storageService.write(testListPath, String.join("\n", testList));

        // 创建多个训练任务
        String[] models = (datasetDO.getAlgoType() == AlgoType.CLASSIFICATION) ? Constants.CLASSIFICATION_MODELS : Constants.DETECTION_MODELS;
        List<Long> algoTrainIdList = new ArrayList<>();
        for (String model : models) {
            try {
                String algoTrainUuid = CommonUtil.generateUuidStartWithAlphabet();

                AlgoTrainCreateRequest request = new AlgoTrainCreateRequest();

                if (datasetDO.getAlgoType() == AlgoType.CLASSIFICATION) {
                    request.setName(String.format("数据集(%s)_自助式算法任务_模型({%s})_训练任务", datasetDO.getName(), model));
                    request.setLanguage(Constants.AUTO_ALGO_TASK_ALGOTRAIN_LANGUAGE);
                    request.setFramework(Constants.AUTO_ALGO_TASK_ALGOTRAIN_FRAMEWORK);
                    request.setResultDirPath("result");
                    request.setEntryAndArgs(String.format(
                            "python one.py --algo_type='%s' --classes_names_str='%s' --train_list_oss_path='%s' --test_list_oss_path='%s'  --model_name='%s' --accuracy_oss_path='%s' --algo_deploy_code_zip_file_oss_path='%s'",
                            datasetDO.getAlgoType().toString(),
                            datasetDO.getClassesNames(),
                            trainListPath,
                            testListPath,
                            model,
                            StorageConstants.getAutoAlgoTaskAlgoTrainAccuracy(uuid, algoTrainUuid),
                            StorageConstants.getAutoAlgoTaskAlgoDeployCodeZipFilePath(uuid, algoTrainUuid)
                    ));
                } else {
                    request.setName(String.format("数据集(%s)_自助式算法任务_模型({%s})_训练任务", datasetDO.getName(), model));
                    request.setLanguage(Language.PYTHON_3_6);
                    request.setFramework(Framework.TENSORFLOW_1_15_0);
                    request.setResultDirPath("result");
                    request.setEntryAndArgs(String.format(
                            "/bin/bash train.sh '%s' '%s' '%s' '%s' '%s' '%s' '%s'",
                            trainListPath,
                            testListPath,
                            datasetDO.getClassesNames(),
                            datasetDO.getClassesNumber(),
                            model,
                            StorageConstants.getAutoAlgoTaskAlgoTrainAccuracy(uuid, algoTrainUuid),
                            StorageConstants.getAutoAlgoTaskAlgoDeployCodeZipFilePath(uuid, algoTrainUuid)
                    ));
                }

                String codeZipFilePath = (datasetDO.getAlgoType() == AlgoType.CLASSIFICATION)
                        ? StorageConstants.getAutoAlgoTaskAlgoTrainCodeZipFilePath()
                        : StorageConstants.getAutoAlgoTaskAlgoTrainDetectionCodeZipFilePath();
                Long algoTrainId = algoTrainService.create(
                        request,
                        codeZipFilePath,
                        algoTrainUuid,
                        requestUsername
                );
                algoTrainIdList.add(algoTrainId);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }

        // 数据库
        AutoAlgoTaskDO autoAlgoTaskDO = new AutoAlgoTaskDO();
        autoAlgoTaskDO.setCreatorName(requestUsername);
        autoAlgoTaskDO.setName(uuid);
        autoAlgoTaskDO.setUuid(uuid);
        autoAlgoTaskDO.setAlgoType(datasetDO.getAlgoType());
        autoAlgoTaskDO.setDatasetId(datasetId);
        autoAlgoTaskDO.setDatasetName(datasetDO.getName());
        autoAlgoTaskDO.setAlgoTrainIdList(StringUtils.join(algoTrainIdList, " "));
        autoAlgoTaskDO.setAlgoDeployId(null);
        autoAlgoTaskDO.setStatus(AutoAlgoTaskStatus.RUNNING);
        autoAlgoTaskMapper.insert(autoAlgoTaskDO);
    }

    @Override
    public List<AutoAlgoTaskDO> list() {
        return autoAlgoTaskMapper.listAll();
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
            for (String algoTrainId : autoAlgoTaskDO.getAlgoTrainIdList().split("\\s+")) {
                AlgoTrainDO algoTrainDO = algoTrainMapper.findById(Long.parseLong(algoTrainId));
                algoTrainList.add(algoTrainDO);
            }
        }

        // 获取训练准确率
        DecimalFormat df = new DecimalFormat("0.0000");
        Map<Long, String> algoTrainIdToAcc = new HashMap<>(algoTrainList.size());
        for (AlgoTrainDO algoTrainDO : algoTrainList) {
            String accPath = StorageConstants.getAutoAlgoTaskAlgoTrainAccuracy(autoAlgoTaskDO.getUuid(), algoTrainDO.getUuid());

            String accStr;
            try {
                accStr = storageService.read(accPath);
            } catch (IOException e) {
                accStr = "unknown";
            }

            String accFormat;
            if (accStr == null) {
                accFormat = "";
            } else if ("unknown".equals(accStr)) {
                accFormat = "unknown";
            } else {
                double accDouble = Double.parseDouble(accStr);
                accFormat = df.format(accDouble);
            }

            algoTrainIdToAcc.put(algoTrainDO.getId(), accFormat);
        }

        // 获取部署详情
        AlgoDeployDO algoDeployDO = algoDeployMapper.findById(autoAlgoTaskDO.getAlgoDeployId());

        // 返回结果
        AutoAlgoTaskDetailResponse response = new AutoAlgoTaskDetailResponse();
        response.setAutoAlgoTask(autoAlgoTaskDO);
        response.setAlgoTrainList(algoTrainList);
        response.setAlgoTrainIdToAcc(algoTrainIdToAcc);
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
            } else if (testList.size() < testSize) {
                testList.add(line);
            }
        }

//        int trainSize = 1000;
//        int testSize = 100;
//        List<String> trainList = new ArrayList<>(trainSize);
//        List<String> testList = new ArrayList<>(testSize);
//
//        for (String line : datasetList) {
//            if (trainList.size() < trainSize) {
//                trainList.add(line);
//            } else if (testList.size() < testSize) {
//                testList.add(line);
//            }
//        }

        return new ImmutablePair<>(trainList, testList);
    }
}
