package cn.lj.pdl.runnable;

import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.constant.StorageConstants;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.dto.algotrain.AlgoTrainCreateRequest;
import cn.lj.pdl.mapper.AlgoTrainMapper;
import cn.lj.pdl.mapper.ImageMapper;
import cn.lj.pdl.model.AlgoTrainDO;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;
import cn.lj.pdl.service.AlgoTrainService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luojian
 * @date 2020/1/4
 */
@Slf4j
public class DatasetImageClusterRunnable implements Runnable {

    private String requestUsername;
    private DatasetDO datasetDO;
    private ImageMapper imageMapper;
    private AlgoTrainMapper algoTrainMapper;
    private StorageService storageService;
    private AlgoTrainService algoTrainService;


    public DatasetImageClusterRunnable(String requestUsername,
                                       DatasetDO datasetDO,
                                       ImageMapper imageMapper,
                                       AlgoTrainMapper algoTrainMapper,
                                       StorageService storageService,
                                       AlgoTrainService algoTrainService) {
        this.requestUsername = requestUsername;
        this.datasetDO = datasetDO;
        this.imageMapper = imageMapper;
        this.algoTrainMapper = algoTrainMapper;
        this.storageService = storageService;
        this.algoTrainService = algoTrainService;
    }

    @Override
    public void run() {
        // 生成图像聚类任务的uuid
        String uuid = CommonUtil.generateUuid();
        log.info("image cluster task uuid: {}", uuid);

        // 文件服务
        storageService.createDirs(
                StorageConstants.getImageClusterTaskRootPath(),
                StorageConstants.getImageClusterTaskDirPath(uuid)
        );
        List<String> dataList = getDataList();
        String dataListPath = StorageConstants.getImageClusterTaskDataPath(uuid);
        storageService.write(dataListPath, String.join("\n", dataList));

        // 创建任务
        AlgoTrainCreateRequest request = new AlgoTrainCreateRequest();
        request.setName(String.format("数据集(%s)_图像聚类任务", datasetDO.getName()));
        request.setLanguage(Constants.IMAGE_CLUSTER_TASK_ALGOTRAIN_LANGUAGE);
        request.setFramework(Constants.IMAGE_CLUSTER_TASK_ALGOTRAIN_FRAMEWORK);
        request.setResultDirPath("result");
        request.setEntryAndArgs(String.format(
                "python main.py --total_cluster_number='%s' --data_list_oss_path='%s' --result_oss_path='%s'",
                datasetDO.getClassesNumber(),
                dataListPath,
                StorageConstants.getImageClusterTaskResultPath(uuid)
        ));

        Long algoTrainId = algoTrainService.create(
                request,
                StorageConstants.getImageClusterTaskAlgotrainCodeZipFilePath(),
                CommonUtil.generateUuidStartWithAlphabet(),
                requestUsername
        );

        // 等待图像聚类任务结束
        while (true) {
            TrainStatus status = algoTrainMapper.findById(algoTrainId).getStatus();
            if (TrainStatus.RUNNING.equals(status)) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (TrainStatus.SUCCESS.equals(status)) {
                break;
            } else {
                log.error("image cluster task failed! taskUuid:{} algoTrainId:{}", uuid, algoTrainId);
                return;
            }
        }

        // 更新图片的聚类编号
        updateImagesClusterNumber(uuid);

        // 更新图片的预测类别
        updatePredictClassName();
    }

    private List<String> getDataList() {
        List<String> dataList = new ArrayList<>(datasetDO.getImagesNumber());
        List<Pair<Long, String>> idToFilenameList = imageMapper.getIdToFilenameList(datasetDO.getId());
        // 生成数据集的文本行以供python程序下载数据, 每行格式: imageId imagePath
        for (Pair<Long, String> p : idToFilenameList) {
            Long imageId = p.getLeft();
            String filename = p.getRight();
            String imagePath = StorageConstants.getDatasetImagePath(datasetDO.getUuid(), filename);
            String line = imageId + " " + imagePath;
            dataList.add(line);
        }

        return dataList;
    }

    private void updateImagesClusterNumber(String taskUuid) {
        String resultPath = StorageConstants.getImageClusterTaskResultPath(taskUuid);
        String result;
        try {
            result = storageService.read(resultPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("storage service read IOException");
        }

        String[] lines = result.split("\n");
        for (String line : lines) {
            String[] sp = line.split("\\s+");
            if (sp.length != 2) {
                log.error("image cluster result line format error");
                continue;
            }
            Long imageId = Long.parseLong(sp[0]);
            Integer clusterNumber = Integer.parseInt(sp[1]);
            imageMapper.updateClusterNumber(imageId, clusterNumber);
        }
    }

    private void updatePredictClassName() {
        int totalClusterNumber = datasetDO.getClassesNumber();
        String[] classesNamesArr = datasetDO.getClassesNames().split("\\s+");

        for (int clusterNumber = 0; clusterNumber < totalClusterNumber; clusterNumber++) {
            System.out.println("clusterNumber: " + clusterNumber);
            // 统计同一个聚类内，每个class已标注的数量，选择最多的那个class，当做这个聚类内未标注图片的预测类别
            Map<String, Integer> className2ImagesNumber = new HashMap<>(classesNamesArr.length);
            for (String className : classesNamesArr) {
                ImageDO condition = new ImageDO();
                condition.setDatasetId(datasetDO.getId());
                condition.setAnnotated(true);
                condition.setAnnotation(className);
                condition.setClusterNumber(clusterNumber);
                Integer imagesNumber = imageMapper.countByCondition(condition);
                className2ImagesNumber.put(className, imagesNumber);
            }

            System.out.println(className2ImagesNumber);

            String maxClassName = null;
            Integer maxImagesNumber = 0;
            for (Map.Entry<String, Integer> entry : className2ImagesNumber.entrySet()) {
                String className = entry.getKey();
                Integer imagesNumber = entry.getValue();
                if (imagesNumber > maxImagesNumber) {
                    maxImagesNumber = imagesNumber;
                    maxClassName = className;
                }
            }

            System.out.println(maxClassName + " " + maxImagesNumber);

            if (maxClassName != null) {
                imageMapper.updatePredictClassName(datasetDO.getId(), clusterNumber, maxClassName);
            }
        }
    }
}
