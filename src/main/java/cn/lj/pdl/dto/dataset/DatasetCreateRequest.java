package cn.lj.pdl.dto.dataset;

import cn.lj.pdl.constant.AlgoType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author luojian
 * @date 2019/11/25
 */
@ApiModel
@Data
public class DatasetCreateRequest {

    @ApiModelProperty(value = "数据集名称", required = true, example = "猫狗分类", position = 1)
    @NotBlank(message = "数据集名称不可为空")
    private String name;

    @ApiModelProperty(value = "数据集描述", required = true, example = "这是一个猫狗分类任务的数据集", position = 2)
    @NotBlank(message = "数据集描述不可为空")
    private String description;

    @ApiModelProperty(value = "算法类型", required = true, example = "CLASSIFICATION", position = 3)
    @NotNull(message = "数据集算法类型不可为空")
    private AlgoType algoType;

    @ApiModelProperty(value = "类别数目", required = true, example = "2", position = 4)
    @NotNull(message = "数据集类别数目不可为空")
    private Integer classesNumber;

    @ApiModelProperty(value = "类别名称列表", required = true, example = "[\"cat\", \"dog\"]", position = 5)
    @NotNull(message = "数据集类别名称列表不可为空")
    private List<String> classesNameList;

}
