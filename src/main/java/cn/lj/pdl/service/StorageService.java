package cn.lj.pdl.service;

import java.net.URL;

/**
 * @author luojian
 * @date 2019/11/29
 */
public interface StorageService {
    /**
     * 存储文件
     *
     * @param path 文件路径
     * @param file 待上传的文件字节流
     * @return URL
     */
    URL uploadFile(String path, byte[] file);

    /**
     * 删除文件
     *
     * @param path 文件路径
     * @return Boolean
     */
    Boolean deleteFile(String path);

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @return Boolean
     */
    Boolean createDir(String path);

    /**
     * 创建多个目录
     *
     * @param paths 目录路径
     * @return Boolean
     */
    Boolean createDirs(String... paths);

    /**
     * 删除目录
     *
     * @param path 目录路径
     * @return Boolean
     */
    Boolean deleteDir(String path);
}
