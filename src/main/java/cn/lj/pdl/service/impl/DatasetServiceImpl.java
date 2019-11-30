package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.mapper.DatasetMapper;
import cn.lj.pdl.mapper.ImageMapper;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;
import cn.lj.pdl.service.DatasetService;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author luojian
 * @date 2019/11/25
 */
@Service
public class DatasetServiceImpl implements DatasetService {
    private DatasetMapper datasetMapper;
    private ImageMapper imageMapper;
    private StorageService storageService;

    @Autowired
    public DatasetServiceImpl(DatasetMapper datasetMapper,
                              ImageMapper imageMapper,
                              StorageService storageService) {
        this.datasetMapper = datasetMapper;
        this.imageMapper = imageMapper;
        this.storageService = storageService;
    }

    @Override
    public PageResponse<DatasetDO> list(Integer pageNumber, Integer pageSize,
                                        String creatorName, String name, AlgoType algoType) {
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize);

        // mapper.xml的条件查询对于null的查询字段会自动忽略，set的这几个字段均可为null，无需另加冗余判断
        DatasetDO condition = new DatasetDO();
        condition.setCreatorName(creatorName);
        condition.setName(name);
        condition.setAlgoType(algoType);

        // 统计符合条件的数据行数
        Integer totalItemsNumber = datasetMapper.countByCondition(condition, pageInfo);

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageSize));

        // 获取符合条件的数据，如果查询的页面超出了数据条数范围，那就无需再查数据库
        List<DatasetDO> list = pageInfo.getStartIndex() >= totalItemsNumber
                ? new ArrayList<>()
                : datasetMapper.findByCondition(condition, pageInfo);

        return new PageResponse<>(pageNumber, pageSize, totalItemsNumber, totalPagesNumber, list);
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

        Long id = datasetMapper.insert(datasetDO);

        try {
            // 创建数据集的目录树
            String datasetRootPath = Constants.getDatasetRootPath();
            String datasetDirPath = Constants.getDatasetDirPath(datasetDO.getUuid());
            String datasetImagesDirPath = Constants.getDatasetImagesDirPath(datasetDO.getUuid());
            String datasetAnnotationsDirPath = Constants.getDatasetAnnotationsDirPath(datasetDO.getUuid());
            storageService.createDirs(datasetRootPath, datasetDirPath, datasetImagesDirPath, datasetAnnotationsDirPath);
        } catch (RuntimeException e) {
            // 文件服务失败，删除刚刚插入数据库的记录
            datasetMapper.delete(id);
            throw e;
        }
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
            throw new BizException(BizExceptionEnum.NOT_THIS_DATASET_CREATOR);
        }

        // 因为建立了外键约束 ON DELETE CASCADE, 所以当数据集被删除时, 所属的图片会被自动删除
        datasetMapper.delete(id);

        // 文件服务删除对应目录
        String datasetDirPath = Constants.getDatasetDirPath(datasetDO.getUuid());
        storageService.deleteDir(datasetDirPath);
    }

    @Override
    public void uploadImage(Long datasetId, byte[] image, String extension, String requestUsername) {
        DatasetDO datasetDO = datasetMapper.findById(datasetId);

        // id 不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        String fileName = CommonUtil.generateUuid() + "." + extension;
        String datasetImagePath = Constants.getDatasetImagePath(datasetDO.getUuid(), fileName);
        // 文件服务上传对应文件
        URL url = storageService.uploadFile(datasetImagePath, image);

        ImageDO imageDO = new ImageDO();
        imageDO.setUploaderName(requestUsername);
        imageDO.setDatasetId(datasetId);
        imageDO.setFilename(fileName);
        imageDO.setAnnotated(false);
        imageDO.setAnnotation(null);
        imageDO.setUrl(url.toString());

        // 对应的数据集的imagesNumber+1已经写在该insert sql中了
        imageMapper.insert(imageDO);
}

    @Override
    public void deleteImage(Long imageId, String requestUsername) {
        ImageDO imageDO = imageMapper.findById(imageId);

        // id 不存在
        if (imageDO == null) {
            throw new BizException(BizExceptionEnum.IMAGE_NOT_EXIST);
        }

        DatasetDO datasetDO = datasetMapper.findById(imageDO.getDatasetId());
        // 不是该数据集的创建者
        if (!datasetDO.getCreatorName().equals(requestUsername)) {
            throw new BizException(BizExceptionEnum.NOT_THIS_DATASET_CREATOR);
        }

        // 文件服务删除对应文件
        storageService.deleteFile(Constants.getDatasetImagePath(datasetDO.getUuid(), imageDO.getFilename()));

        // 对应的数据集的imagesNumber-1已经写在该delete sql中了
        imageMapper.delete(imageId);
    }

    @Override
    public void uploadCoverImage(Long datasetId, byte[] image, String extension) {
        DatasetDO datasetDO = datasetMapper.findById(datasetId);

        // id 不存在
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        String datasetCoverImagePath = Constants.getDatasetCoverImagePath(datasetDO.getUuid(), extension);
        URL url = storageService.uploadFile(datasetCoverImagePath, image);

        // 封面图片信息不需要插入image表，只需要更新dataset表对应行即可
        datasetMapper.updateCoverImageUrl(datasetId, url.toString());
    }

    @Override
    public PageResponse<ImageDO> listImages(Long datasetId,
                                            Integer pageNumber, Integer pageSize,
                                            Boolean annotated) {
        PageInfo pageInfo = new PageInfo(pageNumber, pageSize);

        // mapper.xml的条件查询对于null的查询字段会自动忽略，set的这几个字段均可为null，无需另加冗余判断
        ImageDO condition = new ImageDO();
        condition.setDatasetId(datasetId);
        condition.setAnnotated(annotated);

        // 统计符合条件的数据行数
        Integer totalItemsNumber = imageMapper.countByCondition(condition, pageInfo);

        // 计算总页数, 起码 1 页(即使数据行数 == 0)
        Integer totalPagesNumber = Math.max(1, (int) Math.ceil(totalItemsNumber * 1.0 / pageSize));

        // 获取符合条件的数据，如果查询的页面超出了数据条数范围，那就无需再查数据库
        List<ImageDO> list = pageInfo.getStartIndex() >= totalItemsNumber
                ? new ArrayList<>()
                : imageMapper.findByCondition(condition, pageInfo);

        return new PageResponse<>(pageNumber, pageSize, totalItemsNumber, totalPagesNumber, list);
    }


    private void verifyDatasetCreateRequest(DatasetCreateRequest request) {
        // 数据集名称已存在
        if (datasetMapper.existsByName(request.getName())) {
            throw new BizException(BizExceptionEnum.DATASET_NAME_EXIST);
        }

        switch (request.getAlgoType()) {
            // 对于分类任务，类别数目小于2是不允许的
            case CLASSIFICATION:
                if (request.getClassesNumber() < Constants.CLASSIFICATION_MIN_CLASSES_NUMBER) {
                    throw new BizException(BizExceptionEnum.CLASSIFICATION_CLASSES_NUMBER_LESS_THAN_TWO);
                }
                break;

            // 对于检测任务，类别数目小于1是不允许的
            case DETECTION:
                if (request.getClassesNumber() < Constants.DETECTION_MIN_CLASSES_NUMBER) {
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

}
