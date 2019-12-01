package cn.lj.pdl.mapper;

import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.model.AlgoTrainDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author luojian
 * @date 2019/11/30
 */
@Mapper
@Repository
public interface AlgoTrainMapper {

    /**
     * 插入数据库
     *
     * @param algoTrainDO algoTrainDO
     * @return Long 主键
     */
    Long insert(AlgoTrainDO algoTrainDO);

    /**
     * 根据id 获取 AlgoTrainDO
     *
     * @param id 数据集id
     * @return DatasetDO
     */
    AlgoTrainDO findById(@Param("id") Long id);

    /**
     * 条件查询，返回符合条件的总行数
     *
     * @param condition 查询条件
     * @param pageInfo 页信息
     * @return Integer
     */
    Integer countByCondition(@Param("condition") AlgoTrainDO condition,
                             @Param("pageInfo") PageInfo pageInfo);

    /**
     * 条件查询，返回符合条件的数据列表
     *
     * @param condition 查询条件
     * @param pageInfo 页信息
     * @return List
     */
    List<AlgoTrainDO> findByCondition(@Param("condition") AlgoTrainDO condition,
                                     @Param("pageInfo") PageInfo pageInfo);



}
