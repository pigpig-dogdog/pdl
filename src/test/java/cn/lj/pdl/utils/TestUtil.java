package cn.lj.pdl.utils;

import cn.lj.pdl.dto.user.UserRegisterRequest;
import cn.lj.pdl.model.UserDO;
import cn.lj.pdl.repository.UserRepository;
import cn.lj.pdl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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

    public static Pair<String, String> registerTestUser() {
        String username = generateUUID();
        String password = generateUUID();
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        userService.register(request);
        return Pair.of(username, password);
    }

    public static void removeTestUser(String testUsername) {
        UserDO userDO = userRepository.findByUsername(testUsername);
        userRepository.delete(userDO);
    }


}
