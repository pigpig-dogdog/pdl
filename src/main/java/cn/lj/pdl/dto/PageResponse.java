package cn.lj.pdl.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author luojian
 * @date 2019/11/27
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    @ApiModelProperty(value = "页号", example = "1", position = 1)
    private Integer pageNumber;

    @ApiModelProperty(value = "每页记录数", example = "10", position = 2)
    private Integer pageSize;

    @ApiModelProperty(value = "总项数", example = "105", position = 3)
    private Integer totalItemsNumber;

    @ApiModelProperty(value = "总页数", example = "11", position = 4)
    private Integer totalPagesNumber;

    @ApiModelProperty(value = "数据列表", position = 5)
    private List<T> list;
}
