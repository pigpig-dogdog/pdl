package cn.lj.pdl.mapper;

import cn.lj.pdl.model.ImageDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

        ImageDO imageDO = new ImageDO();
        imageDO.setUploaderName("123");
        imageDO.setDatasetId(1L);
        imageDO.setFilename("test2.jpg");
        imageDO.setAnnotated(false);
        imageDO.setAnnotation(null);
        imageDO.setUrl("123");


        imageMapper.insert(imageDO);

    }

}
