package cn.lj.pdl.controller;

import cn.lj.pdl.model.Body;
import cn.lj.pdl.model.dto.user.UserLoginRequest;
import cn.lj.pdl.model.dto.user.UserLoginResponse;
import cn.lj.pdl.model.dto.user.UserRegisterRequest;
import cn.lj.pdl.model.dto.user.UserRegisterResponse;
import cn.lj.pdl.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "注册成功"),
            @ApiResponse(code = 400, message = "用户名相关：已存在 | 为空 | 长度不在[2, 32]区间内\n密码相关：为空 | 密码长度小于3")})
    public Body<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        UserRegisterResponse response = userService.register(userRegisterRequest);
        return Body.buildSuccess(response);
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "登录成功"),
            @ApiResponse(code = 400, message = "用户名相关：不存在 | 为空\n密码相关：密码错误 | 为空")})
    public Body<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        UserLoginResponse response = userService.login(userLoginRequest);
        return Body.buildSuccess(response);
    }
}