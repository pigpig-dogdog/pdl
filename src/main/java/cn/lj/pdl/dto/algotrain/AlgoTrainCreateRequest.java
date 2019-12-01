package cn.lj.pdl.dto.algotrain;

import cn.lj.pdl.constant.Framework;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author luojian
 * @date 2019/11/30
 */
@ApiModel
@Data
public class AlgoTrainCreateRequest {

    @ApiModelProperty(value = "训练任务名称", required = true, example = "test_train", position = 1)
    @NotBlank(message = "训练任务名称不可为空")
    private String name;

    @ApiModelProperty(value = "深度学习框架", required = true, example = "TENSORFLOW", position = 2)
    @NotNull(message = "深度学习框架不可为空")
    private Framework framework;

    @ApiModelProperty(value = "程序入口与参数（相对路径）", required = true, example = "src/main.py --a=1 --b=2", position = 3)
    @NotBlank(message = "程序入口与参数不可为空")
    private String entryAndArgs;

    @ApiModelProperty(value = "结果目录文件路径（相对路径）", required = true, example = "result/", position = 4)
    @NotBlank(message = "结果目录文件路径不可为空")
    private String resultDirPath;
}
