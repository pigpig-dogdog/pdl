package cn.lj.pdl.model;

import lombok.Data;

import java.util.Date;

/**
 * @author luojian
 * @date 2019/11/24
 */
@Data
public class ImageDO {
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
     * 上传者用户名
     */
    private String uploaderName;

    /**
     * 所属数据集的id
     * 因为建立了外键约束 ON DELETE CASCADE,
     * 所以当数据集被删除时, 所属的图片会被自动删除
     */
    private Long datasetId;

    /**
     * 图片文件名
     * filename = uuid.extension
     */
    private String filename;

    /**
     * 图片是否被标注
     */
    private Boolean annotated;

    /**
     * 标注信息
     *  - 对于检测任务: 保存 xml
     *  - 对于分类任务: 保存 类名
     */
    private String annotation;

    /**
     * 图片url，目前存在OSS上
     */
    private String url;

}
