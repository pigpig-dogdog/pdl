package cn.lj.pdl.dto.dataset;

import cn.lj.pdl.model.ImageDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author luojian
 * @date 2019/12/17
 */
@ApiModel
@Data
public class BatchImagesResponse {
    @ApiModelProperty(value = "下一次获取的起始imageId（不包含）", position = 1)
    private Long startImageId;

    @ApiModelProperty(value = "下一次获取的聚类类别，null表示获取未被聚类的图片", position = 2)
    private Integer clusterNumber;

    @ApiModelProperty(value = "待标注的图片列表", position = 3)
    private List<ImageDO> list;
}
