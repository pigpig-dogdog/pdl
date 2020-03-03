package cn.lj.pdl.service;

import cn.lj.pdl.dto.dataset.annotation.DetectionBbox;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luojian
 * @date 2019/12/11
 */
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class DatasetServiceTest {
    @Test
    public void testJson() {
        DetectionBbox bbox1 = new DetectionBbox();
        bbox1.setX(0);
        bbox1.setY(0);
        bbox1.setWidth(10);
        bbox1.setHeight(10);

        DetectionBbox bbox2 = new DetectionBbox();
        bbox2.setX(50);
        bbox2.setY(50);
        bbox2.setWidth(100);
        bbox2.setHeight(100);

        List<DetectionBbox> bboxes = new ArrayList<>();
        bboxes.add(bbox1);
        bboxes.add(bbox2);

        String annotation = JSON.toJSONString(bboxes);
        System.out.println(annotation);

        List<DetectionBbox> bboxes2 = JSON.parseArray(annotation, DetectionBbox.class);
        System.out.println(bboxes2);
    }
}
