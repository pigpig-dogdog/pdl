package cn.lj.pdl.controller;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.dto.Body;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.dataset.BatchImagesResponse;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.dto.dataset.DatasetImagesNumberDetailResponse;
import cn.lj.pdl.dto.dataset.DatasetModifyRequest;
import cn.lj.pdl.dto.dataset.annotation.AnnotationClassificationRequest;
import cn.lj.pdl.dto.dataset.annotation.AnnotationDetectionRequest;
import cn.lj.pdl.dto.dataset.annotation.DetectionBbox;
import cn.lj.pdl.dto.dataset.annotation.GetPrevOrNextImageResponse;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;
import cn.lj.pdl.service.DatasetService;
import cn.lj.pdl.service.UserService;
import cn.lj.pdl.utils.CommonUtil;
import cn.lj.pdl.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author luojian
 * @date 2019/11/24
 */
@Slf4j
@RestController
@RequestMapping("/dataset")
@Api(tags = "数据集相关接口")
public class DatasetController {

    private UserService userService;
    private DatasetService datasetService;

    @Autowired
    public DatasetController(UserService userService,
                             DatasetService datasetService) {
        this.userService = userService;
        this.datasetService = datasetService;
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取数据集列表", notes = "分页条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "查询页码（默认1）", paramType = "query", dataType = "int", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量（默认10）", paramType = "query", dataType = "int", example = "10"),
            @ApiImplicitParam(name = "creatorName", value = "创建者名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "数据集名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "algoType", value = "算法类型", paramType = "query", dataType = "String")
    })
    public Body<PageResponse<DatasetDO>> list(Integer pageNumber, Integer pageSize,
                                              String creatorName, String name, AlgoType algoType) {
        pageNumber = (pageNumber == null || pageNumber < 1) ? 1 : pageNumber;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        PageResponse<DatasetDO> response = datasetService.list(pageNumber, pageSize, creatorName, name, algoType);
        return Body.buildSuccess(response);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "数据集详情")
    public Body<DatasetDO> detail(@PathVariable("id") Long id) {
        DatasetDO datasetDO = datasetService.detail(id);
        return Body.buildSuccess(datasetDO);
    }

    @GetMapping("/{id}/imagesNumberDetail")
    @ApiOperation(value = "数据集图片数量详情")
    public Body<DatasetImagesNumberDetailResponse> imagesNumberDetail(@PathVariable("id") Long id) {
        DatasetImagesNumberDetailResponse response = datasetService.imagesNumberDetail(id);
        return Body.buildSuccess(response);
    }

    @PostMapping("/create")
    @ApiOperation(value = "创建数据集")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "创建成功"),
            @ApiResponse(code = 400, message = "数据集名称已存在\n算法类别不是['CLASSIFICATION', 'DETECTION']其中之一\n图像分类任务类别数目小于2\n目标检测任务类别数目小于1\n类别数目不等于类名数目\n类名有重复")
    })
    public Body create(@RequestBody @Valid DatasetCreateRequest datasetCreateRequest) {
        datasetService.create(datasetCreateRequest, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除数据集")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 400, message = "数据集不存在\n非数据集创建者无权删除")
    })
    public Body delete(@PathVariable("id") Long id) {
        datasetService.delete(id, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

    @PostMapping("/modify")
    @ApiOperation(value = "修改数据集信息(暂不支持)", hidden = true)
    @ApiResponse(code = 200, message = "修改成功")
    public Body modify(@RequestBody @Valid DatasetModifyRequest datasetModifyRequest) {
        // 暂不支持
        return Body.buildSuccess(null);
    }

    @GetMapping("/{id}/listImages")
    @ApiOperation(value = "获取某个数据集的图像列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "查询页码（默认1）", paramType = "query", dataType = "int", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量（默认10）", paramType = "query", dataType = "int", example = "10"),
            @ApiImplicitParam(name = "annotated", value = "是否已标注", paramType = "query", dataType = "boolean")
    })
    public Body<PageResponse<ImageDO>> listImages(@PathVariable("id") Long id,
                                                  Integer pageNumber, Integer pageSize,
                                                  Boolean annotated, String className) {
        pageNumber = (pageNumber == null || pageNumber < 1) ? 1 : pageNumber;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        className = StringUtils.trimToNull(className);

        PageResponse<ImageDO> response = datasetService.listImages(id, pageNumber, pageSize, annotated, className);
        return Body.buildSuccess(response);
    }

    @PostMapping("/{id}/uploadImage")
    @ApiOperation(value = "数据集上传单张图片/封面")
    public Body uploadImage(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file,
                            @RequestParam(value = "isCoverImage", required = false) Boolean isCoverImage) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new BizException(BizExceptionEnum.EMPTY_FILE);
        }

        if (!FileUtil.isImageFile(file)) {
            throw new BizException(BizExceptionEnum.NOT_IMAGE_FILE);
        }

        byte[] image = file.getBytes();
        String extension = FileUtil.getExtension(file);

        if (isCoverImage != null && isCoverImage) {
            datasetService.uploadCoverImage(id, image, extension);
        } else {
            datasetService.uploadImage(id, image, extension, userService.getCurrentRequestUsername());
        }

        return Body.buildSuccess(null);
    }

    @PostMapping("/{id}/uploadImagesZip")
    public Body uploadImagesZip(@PathVariable("id") Long id,
                                @RequestParam("imagesZipFile") MultipartFile imagesZipFile,
                                @RequestParam("uploadType") int uploadType) throws IOException {

        if (imagesZipFile == null || imagesZipFile.isEmpty()) {
            throw new BizException(BizExceptionEnum.EMPTY_FILE);
        }

        if (!FileUtil.isZipFile(imagesZipFile)) {
            throw new BizException(BizExceptionEnum.NOT_ZIP_FILE);
        }

        if (!datasetService.exist(id)) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        if (uploadType != Constants.UPLOAD_TYPE_UNANNOTATED &&
            uploadType != Constants.UPLOAD_TYPE_CLASSIFICATION &&
            uploadType != Constants.UPLOAD_TYPE_DETECTION) {
            throw new BizException(BizExceptionEnum.UNKNOWN_DATA_UPLOAD_TYPE);
        }

        // 先将压缩文件保存在本地，上传结束之后会删除
        byte[] bytes = imagesZipFile.getBytes();
        String zipFilePath = Paths.get(Constants.TMP_FILE_UPLOAD_FOLDER, CommonUtil.generateUuid() + ".zip").toString();
        Files.write(Paths.get(zipFilePath), bytes);

        datasetService.uploadImagesZip(id, zipFilePath, userService.getCurrentRequestUsername(), uploadType);
        return Body.buildSuccess(null);
    }

    @DeleteMapping("/image/{imageId}")
    @ApiOperation(value = "删除数据集的图片")
    public Body deleteImage(@PathVariable("imageId") Long imageId) {
        datasetService.deleteImage(imageId, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

    @GetMapping("/{id}/getNextBatchUnannotatedImages")
    @ApiOperation(value = "获取下一批未标注的图片【分类】")
    public Body<BatchImagesResponse> getNextBatchUnannotatedImages(@PathVariable("id") Long datasetId,
                                                                   @RequestParam("startImageId") Long startImageId,
                                                                   @RequestParam("batchSize") Integer batchSize,
                                                                   Integer clusterNumber) {
        BatchImagesResponse response = datasetService.getNextBatchUnannotatedImages(datasetId, startImageId, batchSize, clusterNumber);
        return Body.buildSuccess(response);
    }

    @GetMapping("/{id}/getPrevImage")
    @ApiOperation(value = "获取上一张图片【检测】")
    public Body<GetPrevOrNextImageResponse> getPrev(@PathVariable("id") Long datasetId,
                                                    @RequestParam("currentImageId") Long currentImageId) {
        ImageDO imageDO = datasetService.getPrevImage(datasetId, currentImageId);
        List<DetectionBbox> bboxes = JSON.parseArray(imageDO.getAnnotation(), DetectionBbox.class);
        GetPrevOrNextImageResponse response = new GetPrevOrNextImageResponse();
        response.setImageDO(imageDO);
        response.setBboxes(bboxes);
        return Body.buildSuccess(response);
    }

    @GetMapping("/{id}/getNextImage")
    @ApiOperation(value = "获取下一张图片【检测】")
    public Body<GetPrevOrNextImageResponse> getNext(@PathVariable("id") Long datasetId,
                                                    @RequestParam("currentImageId") Long currentImageId) {
        ImageDO imageDO = datasetService.getNextImage(datasetId, currentImageId);
        List<DetectionBbox> bboxes = JSON.parseArray(imageDO.getAnnotation(), DetectionBbox.class);
        GetPrevOrNextImageResponse response = new GetPrevOrNextImageResponse();
        response.setImageDO(imageDO);
        response.setBboxes(bboxes);
        return Body.buildSuccess(response);
    }

    @PostMapping("/{datasetId}/annotationClassification")
    @ApiOperation(value = "图片分类标注")
    public Body annotationClassification(@PathVariable Long datasetId,
                                        @RequestBody AnnotationClassificationRequest request) {
        datasetService.annotationClassification(datasetId, request);
        return Body.buildSuccess(null);
    }

    @PostMapping("/{datasetId}/annotationDetection")
    @ApiOperation(value = "图片检测标注")
    public Body annotationDetection(@PathVariable Long datasetId,
                                    @RequestBody AnnotationDetectionRequest request) {
        datasetService.annotationDetection(datasetId, request);
        return Body.buildSuccess(null);
    }

    @PostMapping("/{id}/createImageClusterTask")
    @ApiOperation(value = "创建图片聚类任务")
    public Body createImageClusterTask(@PathVariable Long id) {
        datasetService.createImageClusterTask(id, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }
}
