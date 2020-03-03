package cn.lj.pdl.mapper;

import cn.lj.pdl.constant.AutoAlgoTaskStatus;
import cn.lj.pdl.model.AutoAlgoTaskDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author luojian
 * @date 2019/12/23
 */
@Mapper
@Repository
public interface AutoAlgoTaskMapper {
    /**
     * 插入数据库
     *
     * @param autoAlgoTaskDO autoAlgoTaskDO
     */
    void insert(AutoAlgoTaskDO autoAlgoTaskDO);

    /**
     * 返回所有数据
     * @return List
     */
    List<AutoAlgoTaskDO> listAll();

    /**
     * 根据id 获取 AutoAlgoTaskDO
     *
     * @param id id
     * @return AutoAlgoTaskDO
     */
    AutoAlgoTaskDO findById(@Param("id") Long id);

    /**
     * 根据 status 获取 AutoAlgoTaskDO 列表
     *
     * @param status status
     * @return List<AutoAlgoTaskDO>
     */
    List<AutoAlgoTaskDO> findByStatus(@Param("status") AutoAlgoTaskStatus status);

    /**
     * 根据 id 更新 status
     *
     * @param id id
     * @param status status
     */
    void updateStatus(@Param("id") Long id, @Param("status") AutoAlgoTaskStatus status);

    /**
     * 根据 id 更新 algoDeployId
     *
     * @param id id
     * @param algoDeployId algoDeployId
     */
    void updateAlgoDeployId(@Param("id") Long id, @Param("algoDeployId") Long algoDeployId);
}
