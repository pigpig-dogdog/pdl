package cn.lj.pdl.controller;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.dto.Body;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.dataset.DatasetCreateRequest;
import cn.lj.pdl.dto.dataset.DatasetModifyRequest;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;
import cn.lj.pdl.service.DatasetService;
import cn.lj.pdl.service.UserService;
import cn.lj.pdl.utils.FileUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

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
    public Body delete(@PathVariable Long id) {
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
    public Body<PageResponse<ImageDO>> listImages(@PathVariable Long id,
                                                  Integer pageNumber, Integer pageSize,
                                                  Boolean annotated) {
        pageNumber = (pageNumber == null || pageNumber < 1) ? 1 : pageNumber;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        PageResponse<ImageDO> response = datasetService.listImages(id, pageNumber, pageSize, annotated);
        return Body.buildSuccess(response);
    }

    @PostMapping("/{id}/uploadImage")
    @ApiOperation(value = "数据集上传单张图片/封面")
    public Body uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file,
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

    @DeleteMapping("/image/{imageId}")
    @ApiOperation(value = "删除数据集的图片【这个功能先不用做】")
    public Body deleteImage(@PathVariable Long imageId) {
        datasetService.deleteImage(imageId, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }
}
