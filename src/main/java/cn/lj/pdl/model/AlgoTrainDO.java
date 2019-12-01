package cn.lj.pdl.model;

import cn.lj.pdl.constant.Framework;
import cn.lj.pdl.constant.TrainStatus;
import lombok.Data;

import java.util.Date;

/**
 * @author luojian
 * @date 2019/11/30
 */
@Data
public class AlgoTrainDO {
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
     * 程序入口与参数
     */
    private String entryAndArgs;

    /**
     * 由用户指定的结果文件（如模型与训练日志）保存的容器本地路径
     * 在用户程序运行结束之后, 容器该路径下的所有文件将被打包上传至OSS
     */
    private String resultDirPath;

    /**
     * uuid唯一标识
     */
    private String uuid;

    /**
     * 训练状态
     */
    private TrainStatus status;

    /**
     * 代码压缩文件的OSS路径
     */
    private String codeZipFilePath;

    /**
     * 结果压缩文件下载的url
     */
    private String resultZipFileUrl;

}
