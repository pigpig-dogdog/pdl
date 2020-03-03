package cn.lj.pdl.model;

import cn.lj.pdl.constant.AlgoType;
import cn.lj.pdl.constant.AutoAlgoTaskStatus;
import lombok.Data;

import java.util.Date;

/**
 * @author luojian
 * @date 2019/12/23
 */
@Data
public class AutoAlgoTaskDO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 创建者用户名
     */
    private String creatorName;

    /**
     * 名称
     */
    private String name;

    /**
     * uuid唯一标识
     */
    private String uuid;

    /**
     * 算法类型 [CLASSIFICATION, DETECTION]
     */
    private AlgoType algoType;

    /**
     * 数据集id
     */
    private Long datasetId;

    /**
     * 数据集名称
     */
    private String datasetName;

    /**
     * 训练id列表
     */
    private String algoTrainIdList;

    /**
     * 部署id
     */
    private Long algoDeployId;

    /**
     * 自助式算法任务状态
     */
    private AutoAlgoTaskStatus status;
}
