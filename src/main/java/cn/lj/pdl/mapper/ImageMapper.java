package cn.lj.pdl.mapper;

import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.model.ImageDO;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
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

    /**
     * 图片标注
     *
     * @param imageId 图片id
     * @param annotation 标注信息，如果是分类任务：class name；如果是检测任务：bboxes json
     */
    void updateAnnotation(@Param("imageId") Long imageId,
                          @Param("annotation") String annotation);

    /**
     * 获取某个数据集的 id -> filename 的列表
     * @param datasetId datasetId
     * @return List
     */
    List<Pair<Long, String>> getIdToFilenameList(@Param("datasetId") Long datasetId);

    /**
     * 更新图片聚类类别
     * @param imageId 图片id
     * @param clusterNumber 聚类类别
     */
    void updateClusterNumber(@Param("imageId") Long imageId,
                             @Param("clusterNumber") Integer clusterNumber);

    /**
     * 更新图片预测类别
     * @param datasetId 数据集id
     * @param clusterNumber 聚类类别
     * @param predictClassName 预测类别
     */
    void updatePredictClassName(@Param("datasetId") Long datasetId,
                                @Param("clusterNumber") Integer clusterNumber,
                                @Param("predictClassName") String predictClassName);
}
