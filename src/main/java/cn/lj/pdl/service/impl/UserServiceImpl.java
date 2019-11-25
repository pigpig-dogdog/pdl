package cn.lj.pdl.service.impl;

import cn.lj.pdl.dto.user.UserLoginRequest;
import cn.lj.pdl.dto.user.UserLoginResponse;
import cn.lj.pdl.dto.user.UserRegisterRequest;
import cn.lj.pdl.dto.user.UserRegisterResponse;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.model.UserDO;
import cn.lj.pdl.repository.UserRepository;
import cn.lj.pdl.security.JwtTokenProvider;
import cn.lj.pdl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public UserRegisterResponse register(UserRegisterRequest request) {
        String username = request.getUsername();

        // 用户名已存在
        if (userRepository.existsByUsername(username)) {
            throw new BizException(BizExceptionEnum.USER_REGISTER_USERNAME_EXIST);
        }

        UserDO userDO = new UserDO();
        userDO.setUsername(username);
        userDO.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(userDO);

        UserRegisterResponse response = new UserRegisterResponse();
        response.setUsername(username);
        response.setToken(jwtTokenProvider.createBearerToken(username));
        return response;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        String username = request.getUsername();

        // 用户不存在
        if (!userRepository.existsByUsername(username)) {
            throw new BizException(BizExceptionEnum.USER_LOGIN_USERNAME_NOT_EXIST);
        }

        // 密码不正确
        UserDO userDO = userRepository.findByUsername(username);
        if (!passwordEncoder.matches(request.getPassword(), userDO.getPassword())) {
            throw new BizException(BizExceptionEnum.USER_LOGIN_PASSWORD_ERROR);
        }

        UserLoginResponse response = new UserLoginResponse();
        response.setUsername(username);
        response.setToken(jwtTokenProvider.createBearerToken(username));
        return response;
    }

}
