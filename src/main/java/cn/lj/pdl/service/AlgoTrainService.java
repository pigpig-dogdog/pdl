package cn.lj.pdl.service;

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
     * @return PageResponse
     */
    PageResponse<AlgoTrainDO> list(Integer pageNumber, Integer pageSize);

}
