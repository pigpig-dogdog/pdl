package cn.lj.pdl.service;

import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algotrain.AlgoTrainCreateRequest;
import cn.lj.pdl.model.AlgoTrainDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author luojian
 * @date 2019/11/30
 */
@Mapper
@Repository
public interface AlgoTrainService {
    /**
     * 创建训练任务
     *
     * @param request 请求
     * @param codeZipFile 代码压缩文件
     * @param requestUsername 请求者用户名
     */
    void create(AlgoTrainCreateRequest request, byte[] codeZipFile, String requestUsername);

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
     */
    String getLog(Long id);
}
