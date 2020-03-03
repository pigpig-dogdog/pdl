package cn.lj.pdl.service.impl;

import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.service.StorageService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    @Value("${aliyun.oss.file-url-expiration}")
    private Long fileUrlExpiration;

    private OSS ossClient;

    @Autowired
    public OssStorageServiceImpl(OSS ossClient) {
        this.ossClient = ossClient;
    }

    @Override
    public URL uploadFile(String path, byte[] file) {
        if (StringUtils.endsWithIgnoreCase(path, "/")) {
            throw new BizException(BizExceptionEnum.FILE_NAME_ENDSWITH_SLASH);
        }
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(file));
        return ossClient.generatePresignedUrl(bucketName, path, new Date(System.currentTimeMillis() + fileUrlExpiration));
    }

    @Override
    public URL uploadLocalFile(String path, String localPath) {
        if (StringUtils.endsWithIgnoreCase(path, "/")) {
            throw new BizException(BizExceptionEnum.FILE_NAME_ENDSWITH_SLASH);
        }
        ossClient.putObject(bucketName, path, new File(localPath));
        return ossClient.generatePresignedUrl(bucketName, path, new Date(System.currentTimeMillis() + fileUrlExpiration));
    }

    @Override
    public Boolean deleteFile(String path) {
        ossClient.deleteObject(bucketName, path);
        return true;
    }

    @Override
    public Boolean createDir(String path) {
        if (!StringUtils.endsWithIgnoreCase(path, "/")) {
            throw new BizException(BizExceptionEnum.DIR_NAME_NOT_ENDSWITH_SLASH);
        }

        ossClient.putObject(bucketName, path, new ByteArrayInputStream(new byte[0]));
        return true;
    }

    @Override
    public Boolean createDirs(String... paths) {
        for (String path : paths) {
            if (!StringUtils.endsWithIgnoreCase(path, "/")) {
                throw new BizException(BizExceptionEnum.DIR_NAME_NOT_ENDSWITH_SLASH);
            }
        }

        for (String path : paths) {
            ossClient.putObject(bucketName, path, new ByteArrayInputStream(new byte[0]));
        }
        return true;
    }

    @Override
    public List<String> listFiles(String path) {
        return listObjectsCore(path, false);
    }

    @Override
    public List<String> listFilesRecursive(String path) {
        return listObjectsCore(path, true);
    }

    @Override
    public Boolean deleteDir(String path) {
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
                    keys.add(s.getKey());
                }
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);
                ossClient.deleteObjects(deleteObjectsRequest);
            }

            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        return true;
    }

    @Override
    public void write(String path, String content) {
        ossClient.putObject(bucketName, path, new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public String read(String path) throws IOException {
        if (!ossClient.doesObjectExist(bucketName, path)) {
            return null;
        }
        OSSObject ossObject = ossClient.getObject(bucketName, path);
        String content = IOUtils.toString(ossObject.getObjectContent(), StandardCharsets.UTF_8);
        ossObject.close();
        return content;
    }

    @Override
    public void copy(String src, String dst) {
        ossClient.copyObject(bucketName, src, bucketName, dst);
    }

    private List<String> listObjectsCore(String path, boolean recursive) {
        List<String> ret = new ArrayList<>();

        String nextMarker = null;
        ObjectListing objectListing = null;

        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
                    .withPrefix(path)
                    .withMarker(nextMarker);

            if (!recursive) {
                listObjectsRequest.setDelimiter("/");
            }

            objectListing = ossClient.listObjects(listObjectsRequest);

            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                if (path.equals(s.getKey())) {
                    // 去除根目录
                    continue;
                }
                ret.add(s.getKey());
            }

            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());

        return ret;
    }
}
