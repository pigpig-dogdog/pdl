package cn.lj.pdl.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author luojian
 * @date 2019/11/27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatasetMapperTest {

    @Autowired
    private DatasetMapper datasetMapper;

    @Test
    public void test() {

//        DatasetDO condition = new DatasetDO();
//        condition.setCreatorName("user");

//        List<DatasetDO> list = datasetMapper.findByCondition(condition, new PageInfo(3, 3));
//        System.out.println(list.size());
//        list.forEach(System.out::println);

//        System.out.println(datasetMapper.findById(18L));

    }
}
