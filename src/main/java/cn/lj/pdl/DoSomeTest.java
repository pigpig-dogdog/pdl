package cn.lj.pdl;

import cn.lj.pdl.dto.dataset.annotation.DetectionBbox;
import cn.lj.pdl.utils.PascalVocXmlParser;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author luojian
 * @date 2019/11/23
 */
public class DoSomeTest {
    public static void main(String[] args) {

//        OssStorageServiceImpl oss = new OssStorageServiceImpl();
//        oss.setBucketName("luojian-pdl");
//        oss.setEndpoint("oss-cn-beijing.aliyuncs.com");
//        oss.setAccessKeyId("");
//        oss.setAccessKeySecret("");
//
//        String src = "test/test.png";
//        String dst = "test/copy_test/test.png";
//        oss.copy(src, dst);

//        List<DetectionBbox> bboxes = PascalVocXmlParser.parse("/Users/luojian/Desktop/vehicle_0002171.xml");
//        System.out.println(bboxes);

        Iterator it = FileUtils.iterateFiles(new File("/Users/luojian/Desktop/small"), null, true);
        while(it.hasNext()) {
            File file = (File) it.next();
            System.out.println(file.getAbsolutePath());
        }
    }
}
