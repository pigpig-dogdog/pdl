package cn.lj.pdl.service;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.dataset.BatchImagesResponse;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.dto.dataset.DatasetImagesNumberDetailResponse;
import cn.lj.pdl.dto.dataset.annotation.AnnotationClassificationRequest;
import cn.lj.pdl.dto.dataset.annotation.AnnotationDetectionRequest;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;

/**
 * @author luojian
 * @date 2019/11/25
 */
public interface DatasetService {

    /**
     * 查询数据集是否存在
     *
     * @param id 数据集id
     * @return boolean
     */
    boolean exist(Long id);

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
     * 数据集标注图片数量与未标注图片数量详情
     * 分类任务：{"total": xxx, "annotated": xxx, "unAnnotated": xxx, "class_name_1": xxx, "class_name_2": xxx}
     * 检测任务：{"total": xxx, "annotated": xxx, "unAnnotated": xxx}
     *
     * @param id 数据集id
     * @return DatasetImagesNumberDetailResponse
     */
    DatasetImagesNumberDetailResponse imagesNumberDetail(Long id);

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
     * 给数据集上传多张图片（压缩包形式）
     *
     * @param datasetId 数据集id
     * @param zipFilePath 压缩文件保存在本地的路径
     * @param requestUsername 发起请求的用户名
     * @param uploadType 上传类型
     */
    void uploadImagesZip(Long datasetId, String zipFilePath, String requestUsername, int uploadType);

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
     */
    void uploadCoverImage(Long datasetId, byte[] image, String extension);

    /**
     * 获取某个数据集的图像列表, 分页条件查询
     *
     * @param datasetId 数据集id
     * @param  pageNumber 页号
     * @param pageSize 页码
     * @param annotated 是否已标注
     * @param className 类别
     * @return PageResponse
     */
    PageResponse<ImageDO> listImages(Long datasetId,
                                     Integer pageNumber, Integer pageSize,
                                     Boolean annotated, String className);

    /**
     * 获取上一张图片
     *
     * @param datasetId 数据集id
     * @param currentImageId 当前图片的id
     * @return ImageDO
     */
    ImageDO getPrevImage(Long datasetId, Long currentImageId);

    /**
     * 获取下一张图片
     *
     * @param datasetId 数据集id
     * @param currentImageId 当前图片的id
     * @return ImageDO
     */
    ImageDO getNextImage(Long datasetId, Long currentImageId);

    /**
     * 获取下一批未标注的图片
     *
     * @param datasetId 数据集id
     * @param startImageId 起始图片id
     * @param batchSize 批大小
     * @param clusterNumber 聚类类别
     * @return BatchImagesResponse
     */
    BatchImagesResponse getNextBatchUnannotatedImages(Long datasetId, Long startImageId, Integer batchSize, Integer clusterNumber);

    /**
     * 分类图片批量标注
     *
     * @param datasetId 数据集id
     * @param request 请求
     */
    void annotationClassification(Long datasetId, AnnotationClassificationRequest request);

    /**
     * 检测图片单张标注
     *
     * @param datasetId 数据集id
     * @param request 请求
     */
    void annotationDetection(Long datasetId, AnnotationDetectionRequest request);

    /**
     * 创建图片聚类任务
     *
     * @param datasetId datasetId
     * @param requestUsername 发起请求的用户名
     */
    void createImageClusterTask(Long datasetId, String requestUsername);
}
