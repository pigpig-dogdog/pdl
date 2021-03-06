package cn.lj.pdl.dto.autoalgotask;

import cn.lj.pdl.model.AlgoDeployDO;
import cn.lj.pdl.model.AlgoTrainDO;
import cn.lj.pdl.model.AutoAlgoTaskDO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author luojian
 * @date 2019/12/24
 */
@Data
public class AutoAlgoTaskDetailResponse {
    private AutoAlgoTaskDO autoAlgoTask;
    private List<AlgoTrainDO> algoTrainList;
    private Map<Long, String> algoTrainIdToAcc;
    private AlgoDeployDO algoDeploy;
}
