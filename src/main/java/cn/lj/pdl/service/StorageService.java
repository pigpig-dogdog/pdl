package cn.lj.pdl.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author luojian
 * @date 2019/11/29
 */
public interface StorageService {
    /**
     * 上传文件
     *
     * @param path 文件路径
     * @param file 待上传的文件字节流
     * @return URL
     */
    URL uploadFile(String path, byte[] file);

    /**
     * 上传本地文件
     *
     * @param path 文件路径
     * @param localPath 待上传的本地文件路径
     * @return URL
     */
    URL uploadLocalFile(String path, String localPath);

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
     * 列举目录下的所有子文件（仅为子文件，不包含目录）
     *
     * @param path 目录路径
     * @return List
     */
    List<String> listFiles(String path);

    /**
     * 递归 列举目录下的所有子文件（仅为子文件，不包含目录）
     *
     * @param path 目录路径
     * @return List
     */
    List<String> listFilesRecursive(String path);

    /**
     * 删除目录
     *
     * @param path 目录路径
     * @return Boolean
     */
    Boolean deleteDir(String path);

    /**
     * 写文件
     *
     * @param path 路径
     * @param content 内容
     */
    void write(String path, String content);

    /**
     * 读文件
     *
     * @param path 路径
     * @return String
     * @throws IOException IOException
     */
    String read(String path) throws IOException;

    /**
     * 拷贝文件
     * @param src 源路径
     * @param dst 目标路径
     */
    void copy(String src, String dst);
}
