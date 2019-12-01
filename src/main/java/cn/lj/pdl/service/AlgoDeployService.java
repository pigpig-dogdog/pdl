package cn.lj.pdl.service;

import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algodeploy.AlgoDeployCreateRequest;
import cn.lj.pdl.model.AlgoDeployDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author luojian
 * @date 2019/12/2
 */
@Mapper
@Repository
public interface AlgoDeployService {
    /**
     * 创建部署任务
     *
     * @param request 请求
     * @param codeZipFile 代码压缩文件
     * @param requestUsername 请求者用户名
     */
    void create(AlgoDeployCreateRequest request, byte[] codeZipFile, String requestUsername);

    /**
     * 获取部署任务列表, 分页条件查询
     *
     * @param pageNumber 页号
     * @param pageSize 每页记录数
     * @param creatorName 创建者用户名
     * @param name 部署任务名称
     * @param framework 深度学习框架
     * @param status 部署状态
     * @return PageResponse
     */
    PageResponse<AlgoDeployDO> list(Integer pageNumber, Integer pageSize,
                                    String creatorName, String name, Framework framework, DeployStatus status);

}
