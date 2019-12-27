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
public class ContainerImageResponse {
    @ApiModelProperty(value = "镜像名称", position = 1)
    private String name;

    @ApiModelProperty(value = "镜像大小", position = 2)
    private String sizeMb;
}
