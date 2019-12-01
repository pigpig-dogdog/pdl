package cn.lj.pdl.service.impl;

import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.constant.WriteMode;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.FileUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author luojian
 * @date 2019/11/29
 */
@Service
public class OssStorageServiceImpl implements StorageService {

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.file-url-expiration}")
    private Long fileUrlExpiration;

    @Override
    public URL uploadFile(String path, byte[] file) {
        if (StringUtils.endsWithIgnoreCase(path, Constants.SYMBOL_SLASH)) {
            path = FileUtil.clearAllSuffixSlash(path);
        }
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file));
        URL url = ossClient.generatePresignedUrl(bucketName, path, new Date(System.currentTimeMillis() + fileUrlExpiration));
        ossClient.shutdown();
        return url;
    }

    @Override
    public Boolean deleteFile(String path) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName, path);
        ossClient.shutdown();
        return true;
    }

    @Override
    public Boolean createDir(String path) {
        if (!StringUtils.endsWithIgnoreCase(path, Constants.SYMBOL_SLASH)) {
            throw new BizException(BizExceptionEnum.DIR_NAME_NOT_ENDSWITH_SLASH);
        }

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(new byte[]{}));
        ossClient.shutdown();
        return true;
    }

    @Override
    public Boolean createDirs(String... paths) {
        for (String path : paths) {
            if (!StringUtils.endsWithIgnoreCase(path, "/")) {
                throw new BizException(BizExceptionEnum.DIR_NAME_NOT_ENDSWITH_SLASH);
            }
        }

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        for (String path : paths) {
            ossClient.putObject(bucketName, path, new ByteArrayInputStream(new byte[]{}));
        }
        ossClient.shutdown();
        return true;
    }

    @Override
    public Boolean deleteDir(String path) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 列举所有包含指定前缀的文件并删除。
        String nextMarker = null;
        ObjectListing objectListing = null;
        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
                    .withPrefix(path)
                    .withMarker(nextMarker);

            objectListing = ossClient.listObjects(listObjectsRequest);
            if (objectListing.getObjectSummaries().size() > 0) {
                List<String> keys = new ArrayList<>();
                for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
                    System.out.println("key name: " + s.getKey());
                    keys.add(s.getKey());
                }
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);
                ossClient.deleteObjects(deleteObjectsRequest);
            }

            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        ossClient.shutdown();
        return true;
    }

    @Override
    public void write(String path, String content, WriteMode writeMode) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        if (writeMode == WriteMode.APPEND) {
            OSSObject ossObject = ossClient.getObject(bucketName, path);
            if (ossObject != null) {
                content = content + ossObject.toString();
            }
        }
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(content.getBytes()));
        ossClient.shutdown();
    }

    @Override
    public String read(String path) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        OSSObject ossObject = ossClient.getObject(bucketName, path);
        String content = ossObject == null ? null : ossObject.toString();
        ossClient.shutdown();
        return content;
    }

}
