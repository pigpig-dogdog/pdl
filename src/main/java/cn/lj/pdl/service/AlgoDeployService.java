package cn.lj.pdl.service;

import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algodeploy.AlgoDeployCreateRequest;
import cn.lj.pdl.model.AlgoDeployDO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author luojian
 * @date 2019/12/2
 */
public interface AlgoDeployService {
    /**
     * 创建部署任务（算法在线化服务）
     *
     * @param request 请求
     * @param codeZipFile 代码压缩文件
     * @param requestUsername 请求者用户名
     * @throws IOException IOException
     */
    void create(AlgoDeployCreateRequest request, MultipartFile codeZipFile, String requestUsername) throws IOException;

    /**
     * 创建部署任务（算法在线化服务）
     *
     * @param request 请求
     * @param codeZipFilePath 代码压缩文件OSS路径
     * @param algoDeployUuid algoDeployUuid
     * @param requestUsername 请求者用户名
     * @return Long id
     */
    Long create(AlgoDeployCreateRequest request, String codeZipFilePath, String algoDeployUuid, String requestUsername);

    /**
     * 暂停部署任务（算法在线化服务）
     *
     * @param id id
     * @param requestUsername 请求者用户名
     */
    void stop(Long id, String requestUsername);

    /**
     * 启动部署任务（算法在线化服务）
     *
     * @param id id
     * @param requestUsername 请求者用户名
     */
    void start(Long id, String requestUsername);

    /**
     * 弹性伸缩部署任务（算法在线化服务）
     *
     * @param id id
     * @param replicas 实例数目
     * @param requestUsername 请求者用户名
     */
    void scale(Long id, int replicas, String requestUsername);

    /**
     * 部署任务（算法在线化服务）更新代码模型
     *
     * @param id id
     * @param codeZipFile 代码压缩文件
     * @param mainClassPath 主类路径（若为null则沿用之前的主类路径）
     * @param requestUsername 请求者用户名
     * @throws IOException IOException
     */
    void updateCodeModel(Long id, MultipartFile codeZipFile, String mainClassPath, String requestUsername) throws IOException;

    /**
     * 获取部署任务（算法在线化服务）列表, 分页条件查询
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
