package cn.lj.pdl.controller;

import cn.lj.pdl.dto.Body;
import cn.lj.pdl.dto.PageResponse;
import cn.lj.pdl.dto.algotrain.AlgoTrainCreateRequest;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.model.AlgoTrainDO;
import cn.lj.pdl.service.AlgoTrainService;
import cn.lj.pdl.service.UserService;
import cn.lj.pdl.utils.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author luojian
 * @date 2019/11/30
 */
@Slf4j
@RestController
@RequestMapping("/algo_train")
@Api(tags = "训练相关接口")
public class AlgoTrainController {

    private UserService userService;
    private AlgoTrainService algoTrainService;

    public AlgoTrainController(UserService userService,
                               AlgoTrainService algoTrainService) {
        this.userService = userService;
        this.algoTrainService = algoTrainService;
    }

    @GetMapping("list")
    @ApiOperation(value = "获取训练任务列表", notes = "分页条件查询")
    public Body<PageResponse<AlgoTrainDO>> list(Integer pageNumber, Integer pageSize) {
        PageResponse<AlgoTrainDO> response = algoTrainService.list(pageNumber, pageSize);
        return Body.buildSuccess(response);
    }

    @PostMapping("create")
    @ApiOperation("创建训练任务")
    public Body create(@Valid AlgoTrainCreateRequest algoTrainCreateRequest,
                       @RequestParam("codeZipFile") MultipartFile codeZipFile) throws IOException {

        if (codeZipFile == null || codeZipFile.isEmpty()) {
            throw new BizException(BizExceptionEnum.EMPTY_FILE);
        }

        if (!FileUtil.isZipFile(codeZipFile)) {
            throw new BizException(BizExceptionEnum.NOT_ZIP_FILE);
        }

        byte[] file = codeZipFile.getBytes();
        algoTrainService.create(algoTrainCreateRequest, file, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

}
