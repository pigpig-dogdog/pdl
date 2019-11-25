package cn.lj.pdl.utils;

import cn.lj.pdl.dto.user.UserRegisterRequest;
import cn.lj.pdl.dto.user.UserRegisterResponse;
import cn.lj.pdl.model.UserDO;
import cn.lj.pdl.repository.UserRepository;
import cn.lj.pdl.service.UserService;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Component
public class TestUtil {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    private static UserService userService;
    private static UserRepository userRepository;

    @Autowired
    public TestUtil(UserService userService,
                    UserRepository userRepository) {
        TestUtil.userService = userService;
        TestUtil.userRepository = userRepository;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static Triple<String, String, String> registerTestUser() {
        String username = generateUUID();
        String password = generateUUID();
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        UserRegisterResponse response = userService.register(request);
        return new ImmutableTriple<>(username, password, response.getToken());
    }

    public static void removeTestUser(String testUsername) {
        UserDO userDO = userRepository.findByUsername(testUsername);
        userRepository.delete(userDO);
    }


}
