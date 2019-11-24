package cn.lj.pdl.service;

import cn.lj.pdl.dto.user.UserLoginRequest;
import cn.lj.pdl.dto.user.UserLoginResponse;
import cn.lj.pdl.dto.user.UserRegisterRequest;
import cn.lj.pdl.dto.user.UserRegisterResponse;

/**
 * @author luojian
 * @date 2019/11/23
 */
public interface UserService {
    /**
     * 用户注册
     *
     * @param request 请求
     * @return UserRegisterResponse
     */
    UserRegisterResponse register(UserRegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 请求
     * @return UserLoginResponse
     */
    UserLoginResponse login(UserLoginRequest request);
}
