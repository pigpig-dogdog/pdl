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
public class ServiceResponse {
    @ApiModelProperty(value = "算法在线化服务名称", position = 1)
    private String algoDeployName;

    @ApiModelProperty(value = "创建者", position = 2)
    private String creator;

    @ApiModelProperty(value = "服务地址", position = 3)
    private String serviceUrl;

    @ApiModelProperty(value = "创建时间", position = 4)
    private String createTime;
}
