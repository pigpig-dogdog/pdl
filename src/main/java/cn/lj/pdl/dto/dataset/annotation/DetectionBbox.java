package cn.lj.pdl.dto.dataset.annotation;

import lombok.Data;

/**
 * @author luojian
 * @date 2020/1/6
 */
@Data
public class DetectionBbox {
    /**
     * 类别名称
     */
    private String className;

    /**
     * 左上角顶点的 x 坐标
     */
    private Integer x;

    /**
     * 左上角顶点的 y 坐标
     */
    private Integer y;

    /**
     * 检测框的宽
     */
    private Integer width;

    /**
     * 检测框的高
     */
    private Integer height;
}
