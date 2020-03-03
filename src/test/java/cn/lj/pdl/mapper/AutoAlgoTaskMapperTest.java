package cn.lj.pdl.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author luojian
 * @date 2019/12/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AutoAlgoTaskMapperTest {

    @Autowired
    private AutoAlgoTaskMapper autoAlgoTaskMapper;

    @Test
    public void test() {
//        AutoAlgoTaskDO autoAlgoTaskDO = new AutoAlgoTaskDO();
//        autoAlgoTaskDO.setCreatorName("testUserName");
//        autoAlgoTaskDO.setName("testName");
//        autoAlgoTaskDO.setUuid(CommonUtil.generateUuid());
//        autoAlgoTaskDO.setAlgoType(AlgoType.CLASSIFICATION);
//        autoAlgoTaskDO.setDatasetId(12L);
//        autoAlgoTaskDO.setDatasetName("testDatasetName");
//        autoAlgoTaskDO.setAlgoTrainIdList("18 19 20");
//        autoAlgoTaskDO.setAlgoDeployId(null);
//        autoAlgoTaskDO.setStatus(AutoAlgoTaskStatus.SUCCESS);
//        autoAlgoTaskMapper.insert(autoAlgoTaskDO);

//        AutoAlgoTaskDO autoAlgoTaskDO = autoAlgoTaskMapper.findById(1L);
//        System.out.println(autoAlgoTaskDO);
//
//        List<AutoAlgoTaskDO> list = autoAlgoTaskMapper.findByStatus(AutoAlgoTaskStatus.RUNNING);
//        System.out.println(list);
//
//        autoAlgoTaskMapper.updateStatus(1L, AutoAlgoTaskStatus.SUCCESS);
//        autoAlgoTaskMapper.updateAlgoDeployId(1L, 12L);

    }


}
