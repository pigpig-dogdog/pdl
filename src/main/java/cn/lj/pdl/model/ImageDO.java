package cn.lj.pdl.model;

import lombok.Data;

import java.util.Date;

/**
 * @author luojian
 * @date 2019/11/24
 */
@Data
public class ImageDO {
    private Long id;
    private Date createTime;
    private Date modifyTime;
    private String creatorUsername;
    private Long datasetId;
    private String url;
}
