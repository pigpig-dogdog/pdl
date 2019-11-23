package cn.lj.pdl.service.impl;

import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.model.UserDO;
import cn.lj.pdl.model.dto.user.UserLoginRequest;
import cn.lj.pdl.model.dto.user.UserLoginResponse;
import cn.lj.pdl.model.dto.user.UserRegisterRequest;
import cn.lj.pdl.model.dto.user.UserRegisterResponse;
import cn.lj.pdl.repository.UserRepository;
import cn.lj.pdl.security.JwtTokenProvider;
import cn.lj.pdl.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public UserRegisterResponse register(UserRegisterRequest request) {
        // 用户名已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BizException(BizExceptionEnum.USER_REGISTER_USERNAME_EXIST);
        }

        UserDO userDO = modelMapper.map(request, UserDO.class);
        userDO.setPassword(passwordEncoder.encode(userDO.getPassword()));
        userRepository.save(userDO);

        UserRegisterResponse response = modelMapper.map(userDO, UserRegisterResponse.class);
        String token = jwtTokenProvider.createBearerToken(request.getUsername());
        response.setToken(token);
        return response;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        // 用户不存在
        if (!userRepository.existsByUsername(request.getUsername())) {
            throw new BizException(BizExceptionEnum.USER_LOGIN_USERNAME_NOT_EXIST);
        }

        // 密码不正确
        UserDO userDO = userRepository.findByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), userDO.getPassword())) {
            throw new BizException(BizExceptionEnum.USER_LOGIN_PASSWORD_ERROR);
        }

        UserLoginResponse response = modelMapper.map(userDO, UserLoginResponse.class);
        String token = jwtTokenProvider.createBearerToken(request.getUsername());
        response.setToken(token);
        return response;
    }

}
