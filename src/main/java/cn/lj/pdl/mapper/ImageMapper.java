package cn.lj.pdl.mapper;

import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.model.ImageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author luojian
 * @date 2019/11/29
 */
@Mapper
@Repository
public interface ImageMapper {
    /**
     * 插入
     *
     * @param imageDO imageDO
     */
    void insert(ImageDO imageDO);

    /**
     * 删除
     *
     * @param id 图片id
     */
    void delete(@Param("id") Long id);

    /**
     * 根据图片id获取ImageDO
     *
     * @param id 图片id
     * @return ImageDO
     */
    ImageDO findById(@Param("id") Long id);

    /**
     * 条件查询，返回符合条件的总行数
     *
     * @param condition 查询条件
     * @return Integer
     */
    Integer countByCondition(@Param("condition") ImageDO condition);

    /**
     * 条件查询，返回符合条件的数据列表
     *
     * @param condition 查询条件
     * @param pageInfo 页信息
     * @return List
     */
    List<ImageDO> findByCondition(@Param("condition") ImageDO condition,
                                 @Param("pageInfo") PageInfo pageInfo);

    /**
     * 获取上一张图片
     *
     * @param datasetId 数据集id
     * @param currentImageId 图片id
     * @return ImageDO
     */
    ImageDO getPrevImage(@Param("datasetId") Long datasetId,
                         @Param("currentImageId") Long currentImageId);

    /**
     * 获取下一张图片
     *
     * @param datasetId 数据集id
     * @param currentImageId 图片id
     * @return ImageDO
     */
    ImageDO getNextImage(@Param("datasetId") Long datasetId,
                         @Param("currentImageId") Long currentImageId);

    /**
     * 获取下一批未标注的图片
     *
     * @param datasetId 数据集id
     * @param startImageId 起始图片id
     * @param batchSize 批大小
     * @param clusterNumber 聚类类别
     * @return List<ImageDO>
     */
    List<ImageDO> getNextBatchUnannotatedImages(@Param("datasetId") Long datasetId,
                                                @Param("startImageId") Long startImageId,
                                                @Param("batchSize") Integer batchSize,
                                                @Param("clusterNumber") Integer clusterNumber);
}
