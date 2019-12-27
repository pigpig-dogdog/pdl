package cn.lj.pdl.mapper;

import cn.lj.pdl.model.UserDO;
import cn.lj.pdl.utils.CommonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;

/**
 * @author luojian
 * @date 2019/11/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert() {
        String username = CommonUtil.generateUuid();
        UserDO userDO = new UserDO();
        userDO.setUsername(username);
        userDO.setPassword("123");
        Long id = userMapper.insert(userDO);
        System.out.println(userDO);

        System.out.println(userMapper.existsByUsername("qwqw"));
        System.out.println(userMapper.existsByUsername(username));

        UserDO userDO1 = userMapper.findByUsername(username);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userDO1.getCreateTime()));

        userMapper.delete(id);
        System.out.println(userMapper.existsByUsername(username));
    }
}
