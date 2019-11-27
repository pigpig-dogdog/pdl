package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.mapper.DatasetMapper;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.service.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public DatasetServiceImpl(DatasetMapper datasetMapper) {
        this.datasetMapper = datasetMapper;
    }

    @Override
    public PageResponse<DatasetDO> list(Integer pageNumber, Integer pageSize, String creatorName, String name, AlgoType algoType) {
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
        datasetDO.setName(request.getName());
        datasetDO.setDescription(request.getDescription());
        datasetDO.setAlgoType(request.getAlgoType());
        datasetDO.setClassesNumber(request.getClassesNumber());
        // 类名列表，先对每个类名trim()，再拼接成字符串，以空格分隔
        datasetDO.setClassesNames(request.getClassesNameList().stream().map(String::trim).collect(Collectors.joining(" ")));
        datasetDO.setImagesNumber(0);
        datasetDO.setCoverImageUrl(null);

        Long id = datasetMapper.insert(datasetDO);

        // todo: oss创建目录

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

        datasetMapper.delete(id);

        // todo: 删除这个数据集在oss上的所有图片，image数据库的所有关联记录

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
