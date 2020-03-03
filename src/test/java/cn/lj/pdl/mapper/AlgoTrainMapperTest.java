package cn.lj.pdl.mapper;

import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.constant.Language;
import cn.lj.pdl.constant.TrainStatus;
import cn.lj.pdl.model.AlgoTrainDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author luojian
 * @date 2019/12/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AlgoTrainMapperTest {
    @Autowired
    private AlgoTrainMapper algoTrainMapper;

    @Test
    public void test() {

        AlgoTrainDO algoTrainDO = new AlgoTrainDO();
        algoTrainDO.setCreatorName("test");
        algoTrainDO.setName("test");
        algoTrainDO.setLanguage(Language.PYTHON_3_6);
        algoTrainDO.setFramework(Framework.KERAS_2_3_1);
        algoTrainDO.setEntryAndArgs("test");
        algoTrainDO.setResultDirPath("test");
        algoTrainDO.setUuid("test");
        algoTrainDO.setStatus(TrainStatus.FAILED);
        algoTrainDO.setCodeZipFilePath("test");
        algoTrainDO.setResultZipFileUrl("test");
        algoTrainMapper.insert(algoTrainDO);
    }
}
