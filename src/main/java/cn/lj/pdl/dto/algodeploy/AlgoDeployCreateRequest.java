package cn.lj.pdl.dto.algodeploy;

import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.constant.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

    @ApiModelProperty(value = "语言", required = true, example = "PYTHON_3_6", position = 2)
    @NotNull(message = "语言不可为空")
    private Language language;

    @ApiModelProperty(value = "深度学习框架", required = true, example = "TENSORFLOW", position = 3)
    @NotNull(message = "深度学习框架不可为空")
    private Framework framework;

    @ApiModelProperty(value = "主类路径", required = true, example = "package.model.MyClass", position = 4)
    @NotBlank(message = "主类路径不可为空")
    private String mainClassPath;

    @ApiModelProperty(value = "实例数目", required = true, example = "1", position = 5)
    @Min(value = 1, message = "实例数目最小为1")
    @Max(value = 10, message = "实例数目最大为10")
    private Integer replicas;
}
