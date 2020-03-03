package cn.lj.pdl.mapper;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author luojian
 * @date 2019/11/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageMapperTest {

    @Autowired
    private ImageMapper imageMapper;

    @Test
    public void test() {

        List<Pair<Long, String>> list = imageMapper.getIdToFilenameList(1L);
        System.out.println(list);
    }

}
