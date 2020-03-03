package cn.lj.pdl.mapper;

import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.model.DatasetDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author luojian
 * @date 2019/11/26
 */
@Mapper
@Repository
public interface DatasetMapper {
    /**
     * 插入数据集
     *
     * @param datasetDO datasetDO
     */
    void insert(DatasetDO datasetDO);

    /**
     * 删除数据集
     *
     * @param id 数据集id
     */
    void delete(@Param("id") Long id);

    /**
     * 查找数据集名称是否存在
     *
     * @param name 数据集名称
     * @return Boolean
     */
    Boolean existsByName(@Param("name") String name);

    /**
     * 查找数据集id是否存在
     *
     * @param id 数据集id
     * @return Boolean
     */
    Boolean existsById(@Param("id") Long id);

    /**
     * 根据数据集id 获取 DatasetDO
     *
     * @param id 数据集id
     * @return DatasetDO
     */
    DatasetDO findById(@Param("id") Long id);

    /**
     * 更新数据集的封面图片url
     *
     * @param id 数据集id
     * @param coverImageUrl 封面图片url
     */
    void updateCoverImageUrl(@Param("id") Long id, @Param("coverImageUrl") String coverImageUrl);

    /**
     * 条件查询，返回符合条件的总行数
     *
     * @param condition 查询条件
     * @return Integer
     */
    Integer countByCondition(@Param("condition") DatasetDO condition);

    /**
     * 条件查询，返回符合条件的数据列表
     *
     * @param condition 查询条件
     * @param pageInfo 页信息
     * @return List
     */
    List<DatasetDO> findByCondition(@Param("condition") DatasetDO condition,
                                   @Param("pageInfo") PageInfo pageInfo);



}
