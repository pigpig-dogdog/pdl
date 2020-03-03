package cn.lj.pdl.mapper;

import cn.lj.pdl.constant.DeployStatus;
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
     */
    void insert(AlgoDeployDO algoDeployDO);

    /**
     * 根据id 获取 AlgoDeployDO
     *
     * @param id id
     * @return AlgoDeployDO
     */
    AlgoDeployDO findById(@Param("id") Long id);

    /**
     * 条件查询，返回符合条件的总行数
     *
     * @param condition 查询条件
     * @return Integer
     */
    Integer countByCondition(@Param("condition") AlgoDeployDO condition);

    /**
     * 条件查询，返回符合条件的数据列表
     *
     * @param condition 查询条件
     * @param pageInfo 页信息
     * @return List
     */
    List<AlgoDeployDO> findByCondition(@Param("condition") AlgoDeployDO condition,
                                      @Param("pageInfo") PageInfo pageInfo);

    /**
     * 根据 status 获取 AlgoDeployDO 列表
     *
     * @param status status
     * @return List<AlgoDeployDO>
     */
    List<AlgoDeployDO> findByStatus(@Param("status") DeployStatus status);

    /**
     * 根据 id 更新 status
     *
     * @param id id
     * @param status status
     */
    void updateStatus(@Param("id") Long id, @Param("status") DeployStatus status);

    /**
     * 根据 id 更新 replicas
     *
     * @param id id
     * @param replicas replicas
     */
    void updateReplicas(@Param("id") Long id, @Param("replicas") Integer replicas);

    /**
     * 根据 id 更新 availableReplicas
     *
     * @param id id
     * @param availableReplicas availableReplicas
     */
    void updateAvailableReplicas(@Param("id") Long id, @Param("availableReplicas") Integer availableReplicas);

    /**
     * 根据 id 更新 serviceUrl
     *
     * @param id id
     * @param serviceUrl serviceUrl
     */
    void updateServiceUrl(@Param("id") Long id, @Param("serviceUrl") String serviceUrl);

    /**
     * 根据 id 更新 codeZipFilePath
     *
     * @param id id
     * @param codeZipFilePath codeZipFilePath
     */
    void updateCodeZipFilePath(@Param("id") Long id, @Param("codeZipFilePath") String codeZipFilePath);

    /**
     * 根据 id 更新 mainClassPath
     *
     * @param id id
     * @param mainClassPath mainClassPath
     */
    void updateMainClassPath(@Param("id") Long id, @Param("mainClassPath") String mainClassPath);
}
