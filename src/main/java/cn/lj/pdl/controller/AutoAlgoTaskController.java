package cn.lj.pdl.controller;

import cn.lj.pdl.dto.Body;
import cn.lj.pdl.dto.autoalgotask.AutoAlgoTaskDetailResponse;
import cn.lj.pdl.service.AutoAlgoTaskService;
import cn.lj.pdl.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author luojian
 * @date 2019/12/10
 */
@Slf4j
@RestController
@RequestMapping("/auto_algo_task")
@Api(tags = "自助式算法任务相关接口")
public class AutoAlgoTaskController {

    private UserService userService;
    private AutoAlgoTaskService autoAlgoTaskService;

    @Autowired
    public AutoAlgoTaskController(UserService userService,
                                  AutoAlgoTaskService autoAlgoTaskService) {
        this.userService = userService;
        this.autoAlgoTaskService = autoAlgoTaskService;
    }

    @PostMapping("/create")
    public Body create(@RequestParam("datasetId") Long datasetId) {
        autoAlgoTaskService.create(datasetId, userService.getCurrentRequestUsername());
        return Body.buildSuccess(null);
    }

    @GetMapping("/{autoAlgoTaskId}/detail")
    public Body<AutoAlgoTaskDetailResponse> detail(@PathVariable Long autoAlgoTaskId) {
        AutoAlgoTaskDetailResponse response = autoAlgoTaskService.detail(autoAlgoTaskId);
        return Body.buildSuccess(response);
    }
}
