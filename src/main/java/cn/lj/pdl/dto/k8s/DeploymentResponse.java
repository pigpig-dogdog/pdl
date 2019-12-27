package cn.lj.pdl.dto.k8s;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luojian
 * @date 2019/12/9
 */
@Data
@ApiModel
public class DeploymentResponse {
    @ApiModelProperty(value = "算法在线化服务名称", position = 1)
    private String algoDeployName;

    @ApiModelProperty(value = "创建者", position = 2)
    private String creator;

    @ApiModelProperty(value = "创建时间", position = 3)
    private String createTime;

    @ApiModelProperty(value = "总实例", position = 4)
    private Integer replicas;

    @ApiModelProperty(value = "可用实例", position = 5)
    private Integer availableReplicas;

    @ApiModelProperty(value = "镜像", position = 6)
    private String image;

    @ApiModelProperty(value = "容器执行的命令", position = 7)
    private String command;

    @ApiModelProperty(value = "命令参数", position = 8)
    private String args;

}
