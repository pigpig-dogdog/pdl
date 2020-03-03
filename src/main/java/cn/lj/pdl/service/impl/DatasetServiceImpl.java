package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.constant.StorageConstants;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.dataset.BatchImagesResponse;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.dto.dataset.DatasetImagesNumberDetailResponse;
import cn.lj.pdl.dto.dataset.annotation.AnnotationClassificationRequest;
import cn.lj.pdl.dto.dataset.annotation.AnnotationDetectionRequest;
import cn.lj.pdl.dto.dataset.annotation.DetectionBbox;
import cn.lj.pdl.dto.dataset.annotation.ImageIdToClassName;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.mapper.AlgoTrainMapper;
import cn.lj.pdl.mapper.DatasetMapper;
import cn.lj.pdl.mapper.ImageMapper;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;
import cn.lj.pdl.runnable.DatasetImageClusterRunnable;
import cn.lj.pdl.runnable.UploadImagesZipRunnable;
import cn.lj.pdl.service.AlgoTrainService;
import cn.lj.pdl.service.DatasetService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author luojian
 * @date 2019/11/25
 */
@Service
public class DatasetServiceImpl implements DatasetService {

    private ExecutorService executorService = new ThreadPoolExecutor(0, 50, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new ThreadFactoryBuilder().setNameFormat("dataset-service-thread-%d").build());

    private DatasetMapper datasetMapper;
    private ImageMapper imageMapper;
    private AlgoTrainMapper algoTrainMapper;
    private StorageService storageService;
    private AlgoTrainService algoTrainService;

    @Autowired
    public DatasetServiceImpl(DatasetMapper datasetMapper,
                              ImageMapper imageMapper,
                              AlgoTrainMapper algoTrainMapper,
                              StorageService storageService,
                              AlgoTrainService algoTrainService) {
        this.datasetMapper = datasetMapper;
        this.imageMapper = imageMapper;
        this.algoTrainMapper = algoTrainMapper;
        this.storageService = storageService;
        this.algoTrainService = algoTrainService;
    }

    @Override
    public boolean exist(Long id) {
        return datasetMapper.existsById(id);
    }

    @Override
    public PageResponse<DatasetDO> list(Integer pageNumber, Integer pageSize, String creatorName, String name, AlgoType algoType) {
        // 分页信息
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize);

        // 条件查询信息
        DatasetDO condition = new DatasetDO();
        condition.setCreatorName(creatorName);
        condition.setName(name);
        condition.setAlgoType(algoType);

        // 统计符合条件的数据行数
        Integer totalItemsNumber = datasetMapper.countByCondition(condition);

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageSize));

        // 获取符合条件的数据，如果查询的页面超出了数据条数范围，那就无需再查数据库
        List<DatasetDO> list = pageInfo.getStartIndex() >= totalItemsNumber
                ? new ArrayList<>()
                : datasetMapper.findByCondition(condition, pageInfo);

        return new PageResponse<>(pageNumber, pageSize, totalItemsNumber, totalPagesNumber, list);
    }

    @Override
    public DatasetDO detail(Long id) {
        DatasetDO datasetDO = datasetMapper.findById(id);

        // id 不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        return datasetDO;
    }

    @Override
    public DatasetImagesNumberDetailResponse imagesNumberDetail(Long id) {
        DatasetDO datasetDO = datasetMapper.findById(id);

        // id 不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        int totalNumber = datasetDO.getImagesNumber();
        int annotatedNumber = 0;
        int unAnnotatedNumber = 0;

        Map<String, Integer> classNameToAnnotatedNumber = null;
        if (AlgoType.CLASSIFICATION.equals(datasetDO.getAlgoType())) {
            classNameToAnnotatedNumber = new HashMap<>(datasetDO.getClassesNumber());
            for (String className : datasetDO.getClassesNames().split("\\s+")) {
                // 统计数据集某个类别已标注的图片数
                ImageDO conditionClassAnnotated = new ImageDO();
                conditionClassAnnotated.setDatasetId(id);
                conditionClassAnnotated.setAnnotated(true);
                conditionClassAnnotated.setAnnotation(className);
                Integer classAnnotatedNumber = imageMapper.countByCondition(conditionClassAnnotated);
                classNameToAnnotatedNumber.put(className, classAnnotatedNumber);

                // 统计数据集已标注的图片数
                annotatedNumber += classAnnotatedNumber;
            }
        } else {
            // 统计数据集已标注的图片数
            ImageDO conditionAnnotated = new ImageDO();
            conditionAnnotated.setDatasetId(id);
            conditionAnnotated.setAnnotated(true);
            annotatedNumber = imageMapper.countByCondition(conditionAnnotated);
        }

        // 统计数据集未标注的图片数
        unAnnotatedNumber = totalNumber - annotatedNumber;

        // 返回
        DatasetImagesNumberDetailResponse response = new DatasetImagesNumberDetailResponse();
        response.setTotalNumber(totalNumber);
        response.setAnnotatedNumber(annotatedNumber);
        response.setUnAnnotatedNumber(unAnnotatedNumber);
        response.setClassNameToAnnotatedNumber(classNameToAnnotatedNumber);
        return response;
    }

    @Override
    public void create(DatasetCreateRequest request, String requestUsername) {
        // 校验参数
        verifyDatasetCreateRequest(request);

        DatasetDO datasetDO = new DatasetDO();
        datasetDO.setCreatorName(requestUsername);
        datasetDO.setUuid(CommonUtil.generateUuid());
        datasetDO.setName(request.getName());
        datasetDO.setDescription(request.getDescription());
        datasetDO.setAlgoType(request.getAlgoType());
        datasetDO.setClassesNumber(request.getClassesNumber());
        // 类名列表，先对每个类名trim()，再拼接成字符串，以空格分隔
        datasetDO.setClassesNames(request.getClassesNameList().stream().map(String::trim).collect(Collectors.joining(" ")));
        datasetDO.setImagesNumber(0);
        datasetDO.setCoverImageUrl(null);

        // dataset表 插入行
        datasetMapper.insert(datasetDO);

        // 文件服务 创建数据集目录树
        storageService.createDirs(
                StorageConstants.getDatasetRootPath(),
                StorageConstants.getDatasetDirPath(datasetDO.getUuid()),
                StorageConstants.getDatasetImagesDirPath(datasetDO.getUuid()),
                StorageConstants.getDatasetAnnotationsDirPath(datasetDO.getUuid())
        );

    }

    @Override
    public void delete(Long id, String requestUsername) {

        DatasetDO datasetDO = datasetMapper.findById(id);

        // id 不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        // 不是该数据集的创建者
        if (!datasetDO.getCreatorName().equals(requestUsername)) {
            throw new BizException(BizExceptionEnum.USER_IS_NOT_CREATOR);
        }

        // dataset表 删除行
        // 因为建立了外键约束 ON DELETE CASCADE, 所以当数据集被删除时, 所属的图片会被自动删除
        datasetMapper.delete(id);

        // 文件服务 删除数据集目录
        storageService.deleteDir(StorageConstants.getDatasetDirPath(datasetDO.getUuid()));
    }

    @Override
    public PageResponse<ImageDO> listImages(Long datasetId, Integer pageNumber, Integer pageSize, Boolean annotated, String className) {
        // 分页信息
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize);

        // 条件查询信息
        ImageDO condition = new ImageDO();
        condition.setDatasetId(datasetId);
        condition.setAnnotated(annotated);
        condition.setAnnotation(className);

        // 统计符合条件的数据行数
        Integer totalItemsNumber = imageMapper.countByCondition(condition);

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageSize));

        // 获取符合条件的数据，如果查询的页面超出了数据条数范围，那就无需再查数据库
        List<ImageDO> list = pageInfo.getStartIndex() >= totalItemsNumber
                ? new ArrayList<>()
                : imageMapper.findByCondition(condition, pageInfo);

        return new PageResponse<>(pageNumber, pageSize, totalItemsNumber, totalPagesNumber, list);
    }

    @Override
    public void uploadImage(Long datasetId, byte[] image, String extension, String requestUsername) {

        DatasetDO datasetDO = datasetMapper.findById(datasetId);

        // datasetId 不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        String fileName = CommonUtil.generateUuid() + "." + extension;
        String datasetImagePath = StorageConstants.getDatasetImagePath(datasetDO.getUuid(), fileName);
        // 文件服务 上传图片
        URL url = storageService.uploadFile(datasetImagePath, image);

        ImageDO imageDO = new ImageDO();
        imageDO.setUploaderName(requestUsername);
        imageDO.setDatasetId(datasetId);
        imageDO.setFilename(fileName);
        imageDO.setAnnotated(false);
        imageDO.setAnnotation(null);
        imageDO.setUrl(url.toString());
        imageDO.setClusterNumber(null);
        imageDO.setPredictClassName(null);
        // image表 插入行, 同时更新所属数据集的images_number++, 已经一起写在sql里面了
        imageMapper.insert(imageDO);
    }

    @Override
    public void uploadImagesZip(Long datasetId, String zipFilePath, String requestUsername, int uploadType) {
        executorService.execute(
                new UploadImagesZipRunnable(
                        datasetMapper,
                        imageMapper,
                        storageService,
                        datasetId,
                        zipFilePath,
                        requestUsername,
                        uploadType
                )
        );
    }

    @Override
    public void uploadCoverImage(Long datasetId, byte[] image, String extension) {

        DatasetDO datasetDO = datasetMapper.findById(datasetId);

        // id 不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        String datasetCoverImagePath = StorageConstants.getDatasetCoverImagePath(datasetDO.getUuid(), extension);
        URL url = storageService.uploadFile(datasetCoverImagePath, image);

        // 封面图片信息不需要插入image表，只需要更新dataset表对应行即可
        datasetMapper.updateCoverImageUrl(datasetId, url.toString());
    }

    @Override
    public void deleteImage(Long imageId, String requestUsername) {

        ImageDO imageDO = imageMapper.findById(imageId);

        // imageId 不存在
        if (imageDO == null) {
            throw new BizException(BizExceptionEnum.IMAGE_NOT_EXIST);
        }

        DatasetDO datasetDO = datasetMapper.findById(imageDO.getDatasetId());

        // 不是该数据集的创建者
        if (!datasetDO.getCreatorName().equals(requestUsername)) {
            throw new BizException(BizExceptionEnum.USER_IS_NOT_CREATOR);
        }

        // 文件服务 删除对应文件
        storageService.deleteFile(StorageConstants.getDatasetImagePath(datasetDO.getUuid(), imageDO.getFilename()));

        // image表 插入行, 同时更新所属数据集的images_number--, 已经一起写在sql里面了
        imageMapper.delete(imageId);
    }

    private void verifyDatasetCreateRequest(DatasetCreateRequest request) {
        // 数据集名称已存在
        if (datasetMapper.existsByName(request.getName())) {
            throw new BizException(BizExceptionEnum.DATASET_NAME_EXIST);
        }

        switch (request.getAlgoType()) {
            // 对于分类任务，类别数目小于2是不允许的
            case CLASSIFICATION:
                if (request.getClassesNumber() < 2) {
                    throw new BizException(BizExceptionEnum.CLASSIFICATION_CLASSES_NUMBER_LESS_THAN_TWO);
                }
                break;

            // 对于检测任务，类别数目小于1是不允许的
            case DETECTION:
                if (request.getClassesNumber() < 1) {
                    throw new BizException(BizExceptionEnum.DETECTION_CLASSES_NUMBER_LESS_THAN_ONE);
                }
                break;

            // 不支持的算法任务
            default:
                throw new BizException(BizExceptionEnum.ALGO_TYPE_ERROR);
        }

        // 类别数目 != 类名数目
        if (request.getClassesNumber() != request.getClassesNameList().size()) {
            throw new BizException(BizExceptionEnum.CLASSES_NUMBER_NOT_EQUAL_TO_CLASSES_NAME_LIST_SIZE);
        }

        // 类名有重复
        Set<String> classesNameSet = new HashSet<>(request.getClassesNameList());
        if (classesNameSet.size() != request.getClassesNameList().size()) {
            throw new BizException(BizExceptionEnum.CLASSES_NAME_LIST_REPEAT);
        }
    }

    @Override
    public ImageDO getPrevImage(Long datasetId, Long currentImageId) {
        return imageMapper.getPrevImage(datasetId, currentImageId);
    }

    @Override
    public ImageDO getNextImage(Long datasetId, Long currentImageId) {
        return imageMapper.getNextImage(datasetId, currentImageId);
    }

    @Override
    public BatchImagesResponse getNextBatchUnannotatedImages(Long datasetId, Long startImageId, Integer batchSize, Integer clusterNumber) {
        // 获取该数据集的类别数目 classesNumber
        Integer classesNumber = datasetMapper.findById(datasetId).getClassesNumber();

        // 尝试获取 聚类编号为 clusterNumber 的图片
        List<ImageDO> list = imageMapper.getNextBatchUnannotatedImages(datasetId, startImageId, batchSize, clusterNumber);
        // 如果结果为空，那么寻找下一个 clusterNumber
        while (list == null || list.isEmpty()) {
            // 再一次的寻找需要将 clusterNumber 进行自增
            // 如果当前 clusterNumber 是 classesNumber-1或者null，则将自增改为null
            clusterNumber = (clusterNumber == null || clusterNumber + 1 == classesNumber) ? null : clusterNumber + 1;
            list = imageMapper.getNextBatchUnannotatedImages(datasetId, startImageId, batchSize, clusterNumber);
            if (clusterNumber == null && list.isEmpty()) {
                // clusterNumber 的寻找顺序是 0, 1, 2, ..., classesNumber-1, null
                // 如果 clusterNumber 为 null，list还为空，说明已经没有未标注的数据了
                break;
            }
        }

        startImageId = list.isEmpty() ? Long.MAX_VALUE : list.get(list.size() - 1).getId();

        if (list.size() < batchSize) {
            clusterNumber = (clusterNumber == null || clusterNumber + 1 == classesNumber) ? null : clusterNumber + 1;
            startImageId = clusterNumber == null ? Long.MAX_VALUE : 0L;
        }

        BatchImagesResponse response = new BatchImagesResponse();
        response.setStartImageId(startImageId);
        response.setClusterNumber(clusterNumber);
        response.setList(list);
        return response;
    }

    @Override
    public void annotationClassification(Long datasetId, AnnotationClassificationRequest request) {
        DatasetDO datasetDO = datasetMapper.findById(datasetId);
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        List<ImageIdToClassName> list = request.getImageIdToClassNameList();
        if (list == null) {
            return;
        }

        for (ImageIdToClassName imageIdToClassName : list) {
            Long imageId = imageIdToClassName.getImageId();
            String className = imageIdToClassName.getClassName();
            ImageDO imageDO = imageMapper.findById(imageId);
            if (imageDO == null) {
                throw new BizException(BizExceptionEnum.IMAGE_NOT_EXIST);
            }

            if (!imageDO.getDatasetId().equals(datasetId)) {
                throw new BizException(BizExceptionEnum.IMAGE_NOT_BELONG_TO_THIS_DATASET);
            }

            String annotationPath = StorageConstants.getDatasetAnnotationPath(datasetDO.getUuid(), imageDO.getFilename());
            storageService.write(annotationPath, className);
            imageMapper.updateAnnotation(imageId, className);
        }
    }

    @Override
    public void annotationDetection(Long datasetId, AnnotationDetectionRequest request) {
        DatasetDO datasetDO = datasetMapper.findById(datasetId);
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        Long imageId = request.getImageId();
        ImageDO imageDO = imageMapper.findById(imageId);
        if (imageDO == null) {
            throw new BizException(BizExceptionEnum.IMAGE_NOT_EXIST);
        }

        List<DetectionBbox> bboxes = request.getBboxes();
        String annotation = JSON.toJSONString(bboxes);
        String annotationPath = StorageConstants.getDatasetAnnotationPath(datasetDO.getUuid(), imageDO.getFilename());
        storageService.write(annotationPath, annotation);
        imageMapper.updateAnnotation(imageId, annotation);
    }

    @Override
    public void createImageClusterTask(Long datasetId, String requestUsername) {
        DatasetDO datasetDO = datasetMapper.findById(datasetId);

        // id 不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        // 不是分类任务
        if (!AlgoType.CLASSIFICATION.equals(datasetDO.getAlgoType())) {
            throw new BizException(BizExceptionEnum.DATASET_ALGO_TYPE_IS_NOT_CLASSIFICATION);
        }

        executorService.execute(
                new DatasetImageClusterRunnable(
                        requestUsername,
                        datasetDO,
                        imageMapper,
                        algoTrainMapper,
                        storageService,
                        algoTrainService
                )
        );
    }

}
