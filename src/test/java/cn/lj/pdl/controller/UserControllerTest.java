package cn.lj.pdl.controller;

import cn.lj.pdl.constant.TestConstant;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.dto.Body;
import cn.lj.pdl.model.UserDO;
import cn.lj.pdl.dto.user.UserLoginRequest;
import cn.lj.pdl.dto.user.UserLoginResponse;
import cn.lj.pdl.dto.user.UserRegisterRequest;
import cn.lj.pdl.dto.user.UserRegisterResponse;
import cn.lj.pdl.repository.UserRepository;
import cn.lj.pdl.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author luojian
 * @date 2019/11/23
 */
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private String testUsername;
    private String testUserPassword;

    @Before
    public void before() {
        Pair<String, String> pair = registerTestUser();
        testUsername = pair.getFirst();
        testUserPassword = pair.getSecond();
        System.out.println(String.format("Before: 测试账号添加成功!(账号:%s, 密码:%s)", testUsername, testUserPassword));
    }

    @After
    public void after() {
        removeTestUser(testUsername);
        System.out.println(String.format("After: 测试账号删除成功!(账号:%s, 密码:%s)", testUsername, testUserPassword));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // 请求参数
        UserLoginRequest param = new UserLoginRequest();
        param.setUsername(testUsername);
        param.setPassword(testUserPassword);
        String content = JSON.toJSONString(param);

        // 设置http请求包
        RequestBuilder requestBuilder = post("/user/login")
                .contentType(TestConstant.APPLICATION_JSON_UTF8) // 请求的Content-Type
                .accept(TestConstant.APPLICATION_JSON_UTF8)      // 响应的Content-Type
                .content(content);

        // 模拟http请求，并获取http响应包
        MockHttpServletResponse httpResponse = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();

        // 获取 status, body
        int status = httpResponse.getStatus();
        Body<UserLoginResponse> body = JSON.parseObject(httpResponse.getContentAsString(),
                new TypeReference<Body<UserLoginResponse>>(){}.getType());

        // 校验
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value()); // 校验 status 200 OK
        Assertions.assertThat(body.getData().getToken()).isNotBlank();  // 校验 token 不为空
    }

    @Test
    public void testLoginFailedUsernameIsEmpty() throws Exception {
        // 请求参数
        UserLoginRequest param = new UserLoginRequest();
        param.setUsername(null);
        param.setPassword(testUserPassword);
        String content = JSON.toJSONString(param);

        // 设置http请求包
        RequestBuilder requestBuilder = post("/user/login")
                .contentType(TestConstant.APPLICATION_JSON_UTF8) // 请求的Content-Type
                .accept(TestConstant.APPLICATION_JSON_UTF8)      // 响应的Content-Type
                .content(content);

        // 模拟http请求，并获取http响应包
        MockHttpServletResponse httpResponse = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();
        Body<UserLoginResponse> body = JSON.parseObject(httpResponse.getContentAsString(),
                new TypeReference<Body<UserLoginResponse>>(){}.getType());

        // 获取 status, body
        int status = httpResponse.getStatus();

        // 校验
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value()); // 校验 status 400 BAD_REQUEST
        Assertions.assertThat(body.getCode()).isEqualTo(BizExceptionEnum.METHOD_ARGUMENT_NOT_VALID_EXCEPTION.getCode()); // 校验错误码
    }

    @Test
    public void testLoginFailedPasswordIsEmpty() throws Exception {
        // 请求参数
        UserLoginRequest param = new UserLoginRequest();
        param.setUsername(testUsername);
        param.setPassword(null);
        String content = JSON.toJSONString(param);

        // 设置http请求包
        RequestBuilder requestBuilder = post("/user/login")
                .contentType(TestConstant.APPLICATION_JSON_UTF8) // 请求的Content-Type
                .accept(TestConstant.APPLICATION_JSON_UTF8)      // 响应的Content-Type
                .content(content);

        // 模拟http请求，并获取http响应包
        MockHttpServletResponse httpResponse = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();

        // 获取 status, body
        int status = httpResponse.getStatus();
        Body<UserLoginResponse> body = JSON.parseObject(httpResponse.getContentAsString(),
                new TypeReference<Body<UserLoginResponse>>(){}.getType());

        // 校验
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value()); // 校验 status 400 BAD_REQUEST
        Assertions.assertThat(body.getCode()).isEqualTo(BizExceptionEnum.METHOD_ARGUMENT_NOT_VALID_EXCEPTION.getCode()); // 校验错误码
    }

    @Test
    public void testLoginFailedUsernameNotExist() throws Exception {
        // 请求参数
        UserLoginRequest param = new UserLoginRequest();
        param.setUsername(generateUUID());
        param.setPassword(testUserPassword);
        String content = JSON.toJSONString(param);

        // 设置http请求包
        RequestBuilder requestBuilder = post("/user/login")
                .contentType(TestConstant.APPLICATION_JSON_UTF8) // 请求的Content-Type
                .accept(TestConstant.APPLICATION_JSON_UTF8)      // 响应的Content-Type
                .content(content);

        // 模拟http请求，并获取http响应包
        MockHttpServletResponse httpResponse = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();

        // 获取 status, body
        int status = httpResponse.getStatus();
        Body<UserLoginResponse> body = JSON.parseObject(httpResponse.getContentAsString(),
                new TypeReference<Body<UserLoginResponse>>(){}.getType());

        // 校验
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value()); // 校验 status 400 BAD_REQUEST
        Assertions.assertThat(body.getCode()).isEqualTo(BizExceptionEnum.USER_LOGIN_USERNAME_NOT_EXIST.getCode()); // 校验错误码
    }

    @Test
    public void testLoginFailedPasswordError() throws Exception {
        // 请求参数
        UserLoginRequest param = new UserLoginRequest();
        param.setUsername(testUsername);
        param.setPassword(testUserPassword + "0");
        String content = JSON.toJSONString(param);

        // 设置http请求包
        RequestBuilder requestBuilder = post("/user/login")
                .contentType(TestConstant.APPLICATION_JSON_UTF8) // 请求的Content-Type
                .accept(TestConstant.APPLICATION_JSON_UTF8)      // 响应的Content-Type
                .content(content);

        // 模拟http请求，并获取http响应包
        MockHttpServletResponse httpResponse = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();

        // 获取 status, body
        int status = httpResponse.getStatus();
        Body<UserLoginResponse> body = JSON.parseObject(httpResponse.getContentAsString(),
                new TypeReference<Body<UserLoginResponse>>(){}.getType());

        // 校验
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value()); // status 400 BAD_REQUEST
        Assertions.assertThat(body.getCode()).isEqualTo(BizExceptionEnum.USER_LOGIN_PASSWORD_ERROR.getCode()); // 校验错误码
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        // 请求参数
        UserRegisterRequest param = new UserRegisterRequest();
        param.setUsername(generateUUID());
        param.setPassword(generateUUID());
        String content = JSON.toJSONString(param);

        // 设置http请求包
        RequestBuilder requestBuilder = post("/user/register")
                .contentType(TestConstant.APPLICATION_JSON_UTF8) // 请求的Content-Type
                .accept(TestConstant.APPLICATION_JSON_UTF8)      // 响应的Content-Type
                .content(content);

        // 模拟http请求，并获取http响应包
        MockHttpServletResponse httpResponse = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();

        // 获取 status, body
        int status = httpResponse.getStatus();
        Body<UserRegisterResponse> body = JSON.parseObject(httpResponse.getContentAsString(),
                new TypeReference<Body<UserRegisterResponse>>(){}.getType());

        // 校验
        Assertions.assertThat(status).isEqualTo(HttpStatus.OK.value()); // 校验 status 200 OK
        Assertions.assertThat(body.getData().getToken()).isNotBlank();  // 校验 token 不为空
    }

    @Test
    public void testRegisterFailedUsernameExist() throws Exception {
        // 请求参数
        UserRegisterRequest param = new UserRegisterRequest();
        param.setUsername(testUsername);
        param.setPassword(testUserPassword);
        String content = JSON.toJSONString(param);

        // 设置http请求包
        RequestBuilder requestBuilder = post("/user/register")
                .contentType(TestConstant.APPLICATION_JSON_UTF8) // 请求的Content-Type
                .accept(TestConstant.APPLICATION_JSON_UTF8)      // 响应的Content-Type
                .content(content);

        // 模拟http请求，并获取http响应包
        MockHttpServletResponse httpResponse = mockMvc.perform(requestBuilder).andDo(print()).andReturn().getResponse();

        // 获取 status, body
        int status = httpResponse.getStatus();
        Body<UserRegisterResponse> body = JSON.parseObject(httpResponse.getContentAsString(),
                new TypeReference<Body<UserRegisterResponse>>(){}.getType());

        // 校验
        Assertions.assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value()); // 校验 status 400 BAD_REQUEST
        Assertions.assertThat(body.getCode()).isEqualTo(BizExceptionEnum.USER_REGISTER_USERNAME_EXIST.getCode()); // 校验错误码
    }


    private String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private Pair<String, String> registerTestUser() {
        String username = generateUUID();
        String password = generateUUID();
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        userService.register(request);
        return Pair.of(username, password);
    }

    private void removeTestUser(String testUsername) {
        UserDO userDO = userRepository.findByUsername(testUsername);
        userRepository.delete(userDO);
    }

}