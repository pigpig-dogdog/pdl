package cn.lj.pdl.mapper;

import cn.lj.pdl.constant.TrainStatus;
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
     */
    void insert(AlgoTrainDO algoTrainDO);

    /**
     * 根据 id 获取 AlgoTrainDO
     *
     * @param id id
     * @return AlgoTrainDO
     */
    AlgoTrainDO findById(@Param("id") Long id);

    /**
     * 条件查询，返回符合条件的总行数
     *
     * @param condition 查询条件
     * @return Integer
     */
    Integer countByCondition(@Param("condition") AlgoTrainDO condition);

    /**
     * 条件查询，返回符合条件的数据列表
     *
     * @param condition 查询条件
     * @param pageInfo 页信息
     * @return List
     */
    List<AlgoTrainDO> findByCondition(@Param("condition") AlgoTrainDO condition,
                                     @Param("pageInfo") PageInfo pageInfo);

    /**
     * 根据 status 获取 AlgoTrainDO 列表
     *
     * @param status status
     * @return List<AlgoTrainDO>
     */
    List<AlgoTrainDO> findByStatus(@Param("status") TrainStatus status);

    /**
     * 根据 id 更新 status
     *
     * @param id id
     * @param status 新状态
     */
    void updateStatus(@Param("id") Long id, @Param("status") TrainStatus status);
}
