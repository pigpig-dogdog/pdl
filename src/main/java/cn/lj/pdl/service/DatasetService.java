package cn.lj.pdl.service;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.model.DatasetDO;

/**
 * @author luojian
 * @date 2019/11/25
 */
public interface DatasetService {
    /**
     * 创建数据集
     *
     * @param request 请求
     * @param requestUsername 请求者用户名
     */
    void create(DatasetCreateRequest request, String requestUsername);

    /**
     * 删除数据集
     *
     * @param id 数据集id
     * @param requestUsername 发起请求的用户名
     */
    void delete(Long id, String requestUsername);

    /**
     * 分页条件查询
     *
     * @param pageNumber 页号
     * @param pageSize 每页记录数
     * @param creatorName 创建者用户名
     * @param name 数据集名称
     * @param algoType 算法类型
     * @return PageResponse
     */
    PageResponse<DatasetDO> list(Integer pageNumber, Integer pageSize, String creatorName, String name, AlgoType algoType);
}
