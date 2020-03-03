package cn.lj.pdl.service;

import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algotrain.AlgoTrainCreateRequest;
import cn.lj.pdl.model.AlgoTrainDO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author luojian
 * @date 2019/11/30
 */
public interface AlgoTrainService {
    /**
     * 创建训练任务
     *
     * @param request 请求
     * @param codeZipFile 代码压缩文件
     * @param requestUsername 请求者用户名
     * @throws IOException IOException
     */
    void create(AlgoTrainCreateRequest request, MultipartFile codeZipFile, String requestUsername) throws IOException;

    /**
     * 创建训练任务
     *
     * @param request 请求
     * @param codeZipFilePath 代码压缩文件OSS路径
     * @param algoTrainUuid algoTrainUuid
     * @param requestUsername 请求者用户名
     * @return Long AlgoTrainDO Id
     */
    Long create(AlgoTrainCreateRequest request, String codeZipFilePath, String algoTrainUuid, String requestUsername);

    /**
     * 获取训练任务列表, 分页条件查询
     *
     * @param pageNumber 页号
     * @param pageSize 每页记录数
     * @param creatorName 创建者用户名
     * @param name 训练任务名称
     * @param framework 深度学习框架
     * @param status 训练状态
     * @return PageResponse
     */
    PageResponse<AlgoTrainDO> list(Integer pageNumber, Integer pageSize,
                                   String creatorName, String name, Framework framework, TrainStatus status);

    /**
     * 获取训练日志
     *
     * @param id 训练id
     * @return String 日志
     * @throws IOException IOException
     */
    String getLog(Long id) throws IOException;
}
