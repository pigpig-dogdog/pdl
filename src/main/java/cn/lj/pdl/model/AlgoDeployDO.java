package cn.lj.pdl.model;

import cn.lj.pdl.constant.DeployStatus;
import cn.lj.pdl.constant.Framework;
import lombok.Data;

import java.util.Date;

/**
 * @author luojian
 * @date 2019/11/30
 */
@Data
public class AlgoDeployDO {
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
     * 名称
     */
    private String name;

    /**
     * 深度学习框架
     */
    private Framework framework;

    /**
     * 代码压缩文件的OSS路径
     */
    private String codeZipFilePath;

    /**
     * 主类路径
     */
    private String mainClassPath;

    /**
     * 在线化服务的实例数目
     */
    private Integer instanceNumber;

    /**
     * uuid唯一标识
     */
    private String uuid;

    /**
     * 部署状态
     */
    private DeployStatus status;

    /**
     * 在线化服务的url
     */
    private String serviceUrl;
}
