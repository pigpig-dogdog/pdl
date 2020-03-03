package cn.lj.pdl.service;

import cn.lj.pdl.dto.autoalgotask.AutoAlgoTaskDetailResponse;
import cn.lj.pdl.model.AutoAlgoTaskDO;

import java.util.List;

/**
 * @author luojian
 * @date 2019/12/24
 */
public interface AutoAlgoTaskService {

    /**
     * 创建自助式算法任务
     *
     * @param datasetId 数据集id
     * @param requestUsername 请求者用户名
     */
    void create(Long datasetId, String requestUsername);

    /**
     * 自助式算法任务细节
     *
     * @param autoAlgoTaskId autoAlgoTaskId
     * @return AutoAlgoTaskDetailResponse
     */
    AutoAlgoTaskDetailResponse detail(Long autoAlgoTaskId);

    /**
     * 自助式算法任务列表
     * @return
     */
    List<AutoAlgoTaskDO> list();
}
