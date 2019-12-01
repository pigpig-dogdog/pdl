package cn.lj.pdl.service;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;

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
     * 获取数据集列表, 分页条件查询
     *
     * @param pageNumber 页号
     * @param pageSize 每页记录数
     * @param creatorName 创建者用户名
     * @param name 数据集名称
     * @param algoType 算法类型
     * @return PageResponse
     */
    PageResponse<DatasetDO> list(Integer pageNumber, Integer pageSize,
                                 String creatorName, String name, AlgoType algoType);

    /**
     * 数据集详情
     *
     * @param id 数据集id
     * @return DatasetDO
     */
    DatasetDO detail(Long id);

    /**
     * 给数据集上传图片
     *
     * @param datasetId 数据集id
     * @param image 图片
     * @param extension 扩展名
     * @param requestUsername 发起请求的用户名
     */
    void uploadImage(Long datasetId, byte[] image, String extension, String requestUsername);

    /**
     * 给数据集删除图片
     *
     * @param imageId 图片id
     * @param requestUsername 发起请求的用户名
     */
    void deleteImage(Long imageId, String requestUsername);

    /**
     * 给数据集上传封面图片
     *
     * @param datasetId 数据集id
     * @param image 图片
     * @param extension 扩展名
     * @param requestUsername 发起请求的用户名
     */
    void uploadCoverImage(Long datasetId, byte[] image, String extension);

    /**
     * 获取某个数据集的图像列表, 分页条件查询
     *
     * @param datasetId 数据集id
     * @param  pageNumber 页号
     * @param pageSize 页码
     * @param annotated 是否已标注
     * @return PageResponse
     */
    PageResponse<ImageDO> listImages(Long datasetId,
                                     Integer pageNumber, Integer pageSize,
                                     Boolean annotated);
}
