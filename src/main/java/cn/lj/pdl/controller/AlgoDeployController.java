package cn.lj.pdl.controller;

import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.dto.Body;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algodeploy.AlgoDeployCreateRequest;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.model.AlgoDeployDO;
import cn.lj.pdl.service.AlgoDeployService;
import cn.lj.pdl.service.UserService;
import cn.lj.pdl.utils.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author luojian
 * @date 2019/12/2
 */
@Slf4j
@RestController
@RequestMapping("/algo_deploy")
@Api(tags = "部署相关接口")
public class AlgoDeployController {

    private UserService userService;
    private AlgoDeployService algoDeployService;

    @Autowired
    public AlgoDeployController(UserService userService,
                                AlgoDeployService algoDeployService) {
        this.userService = userService;
        this.algoDeployService = algoDeployService;
    }

    @GetMapping("/list")
    @ApiOperation(value = "获取部署列表", notes = "分页条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "查询页码（默认1）", paramType = "query", dataType = "int", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量（默认10）", paramType = "query", dataType = "int", example = "10"),
            @ApiImplicitParam(name = "creatorName", value = "创建者名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "name", value = "部署任务名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "framework", value = "深度学习框架", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "部署状态", paramType = "query", dataType = "String")
    })
    public Body<PageResponse<AlgoDeployDO>> list(Integer pageNumber, Integer pageSize,
                                                 String creatorName, String name, Framework framework, DeployStatus status) {

        creatorName = (creatorName == null || StringUtils.isEmpty(creatorName.trim())) ? null : creatorName.trim();
        name = (name == null || StringUtils.isEmpty(name.trim())) ? null : name.trim();

        PageResponse<AlgoDeployDO> response = algoDeployService.list(pageNumber, pageSize, creatorName, name, framework, status);
        return Body.buildSuccess(response);
    }

    @PostMapping("/create")
    @ApiOperation("创建部署任务")
    public Body create(@Valid AlgoDeployCreateRequest algoDeployCreateRequest,
                       @RequestParam("codeZipFile") MultipartFile codeZipFile) throws IOException {
        if (codeZipFile == null || codeZipFile.isEmpty()) {
            throw new BizException(BizExceptionEnum.EMPTY_FILE);
        }

        if (!FileUtil.isZipFile(codeZipFile)) {
            throw new BizException(BizExceptionEnum.NOT_ZIP_FILE);
        }

        algoDeployService.create(algoDeployCreateRequest, codeZipFile, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

    @PostMapping("/stop")
    @ApiOperation("关闭部署任务")
    public Body stop(@RequestParam("id") Long id) {
        algoDeployService.stop(id, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

    @PostMapping("/start")
    @ApiOperation("重新启动部署任务")
    public Body start(@RequestParam("id") Long id) {
        algoDeployService.start(id, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

    @PostMapping("/scale")
    @ApiOperation("弹性伸缩部署任务")
    public Body scale(@RequestParam("id") Long id,
                      @RequestParam("replicas") Integer replicas) {
        if (replicas == null) {
            throw new BizException(BizExceptionEnum.REPLICAS_CAN_NOT_BE_NULL);
        }

        if (replicas < 0) {
            throw new BizException(BizExceptionEnum.REPLICAS_LESS_THEN_ZERO);
        }

        if (replicas > Constants.REPLICAS_MAX_VALUE) {
            throw new BizException(BizExceptionEnum.REPLICAS_GREATER_THEN_MAX_VALUE);
        }

        algoDeployService.scale(id, replicas, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

    @PostMapping("/updateCodeModel")
    @ApiOperation("更新代码模型")
    public Body updateCodeModel(@RequestParam("id") Long id,
                                @RequestParam("codeZipFile") MultipartFile codeZipFile,
                                @RequestParam(value = "mainClassPath", required = false) String mainClassPath) throws IOException {
        if (codeZipFile == null || codeZipFile.isEmpty()) {
            throw new BizException(BizExceptionEnum.EMPTY_FILE);
        }

        if (!FileUtil.isZipFile(codeZipFile)) {
            throw new BizException(BizExceptionEnum.NOT_ZIP_FILE);
        }

        algoDeployService.updateCodeModel(id, codeZipFile, mainClassPath, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

}
