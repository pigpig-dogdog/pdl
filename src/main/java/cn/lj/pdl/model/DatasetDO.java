package cn.lj.pdl.model;

import cn.lj.pdl.constant.AlgoType;
import lombok.Data;

import java.util.Date;

/**
 * @author luojian
 * @date 2019/11/24
 */
@Data
public class DatasetDO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 创建者用户名
     */
    private String creatorName;

    /**
     * 数据集uuid作为唯一标识
     */
    private String uuid;

    /**
     * 数据集名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 算法类型 [CLASSIFICATION, DETECTION]
     */
    private AlgoType algoType;

    /**
     * 类别数目
     */
    private Integer classesNumber;

    /**
     * 类名列表 "cat dog pig"
     */
    private String classesNames;

    /**
     * 图片总数
     */
    private Integer imagesNumber;

    /**
     * 封面url
     */
    private String coverImageUrl;

}
