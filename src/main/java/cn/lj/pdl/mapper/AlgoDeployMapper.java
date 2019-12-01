package cn.lj.pdl.mapper;

import cn.lj.pdl.dto.PageInfo;
import cn.lj.pdl.model.AlgoDeployDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author luojian
 * @date 2019/12/1
 */
@Mapper
@Repository
public interface AlgoDeployMapper {

    /**
     * 插入数据库
     *
     * @param algoDeployDO algoDeployDO
     * @return Long 主键
     */
    Long insert(AlgoDeployDO algoDeployDO);

    /**
     * 根据id 获取 AlgoDeployDO
     *
     * @param id 数据集id
     * @return DatasetDO
     */
    AlgoDeployDO findById(@Param("id") Long id);

    /**
     * 条件查询，返回符合条件的总行数
     *
     * @param condition 查询条件
     * @param pageInfo 页信息
     * @return Integer
     */
    Integer countByCondition(@Param("condition") AlgoDeployDO condition,
                             @Param("pageInfo") PageInfo pageInfo);

    /**
     * 条件查询，返回符合条件的数据列表
     *
     * @param condition 查询条件
     * @param pageInfo 页信息
     * @return List
     */
    List<AlgoDeployDO> findByCondition(@Param("condition") AlgoDeployDO condition,
                                      @Param("pageInfo") PageInfo pageInfo);
}
