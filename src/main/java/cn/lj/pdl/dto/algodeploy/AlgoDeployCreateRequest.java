package cn.lj.pdl.dto.algodeploy;

import cn.lj.pdl.constant.Framework;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author luojian
 * @date 2019/12/2
 */
@Data
public class AlgoDeployCreateRequest {
    @ApiModelProperty(value = "部署任务名称", required = true, example = "test_deploy", position = 1)
    @NotBlank(message = "部署任务名称不可为空")
    private String name;

    @ApiModelProperty(value = "深度学习框架", required = true, example = "TENSORFLOW", position = 2)
    @NotNull(message = "深度学习框架不可为空")
    private Framework framework;

    @ApiModelProperty(value = "主类路径", required = true, example = "package.model.MyClass", position = 3)
    @NotBlank(message = "主类路径不可为空")
    private String mainClassPath;
}
