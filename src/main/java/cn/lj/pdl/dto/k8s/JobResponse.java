package cn.lj.pdl.dto.k8s;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luojian
 * @date 2019/12/10
 */
@Data
@ApiModel
public class JobResponse {
    @ApiModelProperty(value = "算法训练名称", position = 1)
    private String algoTrainName;

    @ApiModelProperty(value = "创建者", position = 2)
    private String creator;

    @ApiModelProperty(value = "开始时间", position = 3)
    private String createTime;

    @ApiModelProperty(value = "结束时间", position = 4)
    private String endTime;

    @ApiModelProperty(value = "状态", position = 5)
    private String status;

    @ApiModelProperty(value = "镜像", position = 6)
    private String image;

    @ApiModelProperty(value = "容器执行的命令", position = 7)
    private String command;

    @ApiModelProperty(value = "命令参数", position = 8)
    private String args;
}
