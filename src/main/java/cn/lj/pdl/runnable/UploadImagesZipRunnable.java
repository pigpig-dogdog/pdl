package cn.lj.pdl.runnable;

import cn.lj.pdl.constant.StorageConstants;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.mapper.DatasetMapper;
import cn.lj.pdl.mapper.ImageMapper;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import cn.lj.pdl.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author luojian
 * @date 2019/12/19
 */
@Slf4j
public class UploadImagesZipRunnable implements Runnable {

    private DatasetMapper datasetMapper;
    private ImageMapper imageMapper;
    private StorageService storageService;
    private Long datasetId;
    private String zipFilePath;
    private String requestUsername;

    public UploadImagesZipRunnable(DatasetMapper datasetMapper,
                                   ImageMapper imageMapper,
                                   StorageService storageService,
                                   Long datasetId,
                                   String zipFilePath,
                                   String requestUsername) {
        this.datasetMapper = datasetMapper;
        this.imageMapper = imageMapper;
        this.storageService = storageService;
        this.datasetId = datasetId;
        this.zipFilePath = zipFilePath;
        this.requestUsername = requestUsername;
    }

    @Override
    public void run() {
        log.info("thread name:{}. datasetId:{}, zipFilePath:'{}'", Thread.currentThread().getName(), datasetId, zipFilePath);

        // 解压压缩包
        String unzipDirPath = FilenameUtils.removeExtension(zipFilePath);
        log.info("unzip '{}' to directory '{}'", zipFilePath, unzipDirPath);
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.extractAll(unzipDirPath);
        } catch (ZipException e) {
            log.error("unzip filed, message:{}", e.getMessage());
            return;
        }
        log.info("unzip done!");

        // 上传图片
        String imagesDirPath = Paths.get(unzipDirPath, "images").toString();
        if (Files.exists(Paths.get(imagesDirPath))) {
            uploadImages(imagesDirPath);
        } else {
            log.error("Directory 'images/' not found in directory '{}'.", unzipDirPath);
        }
        log.info("upload done!");

        // 删除压缩包与解压后的文件夹
        try {
            log.info("delete zip file '{}'", zipFilePath);
            FileUtils.forceDelete(new File(zipFilePath));
            log.info("delete directory '{}'", unzipDirPath);
            FileUtils.forceDelete(new File(unzipDirPath));
        } catch (IOException e) {
            log.error("delete filed, message:{}", e.getMessage());
        }
    }

    private void uploadImages(String imagesDirPath) {
        DatasetDO datasetDO = datasetMapper.findById(datasetId);
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }

        List<String> classesNames = Arrays.asList(datasetDO.getClassesNames().split("\\s+"));
        log.info("classes_names:{}", classesNames);

        if (!Files.exists(Paths.get(imagesDirPath))) {
            return;
        }

        // 列举类别子文件夹，比如 ['images/cat/', 'images/dog/', ...]
        File[] classesDirList = new File(imagesDirPath).listFiles();
        if (classesDirList == null) {
            return;
        }
        Arrays.sort(classesDirList);

        // 遍历类别子文件夹
        for (File classDirPath : classesDirList) {
            // 判别是否为文件夹
            if (!classDirPath.isDirectory()) {
                continue;
            }

            // 判断类别子文件夹名是否属于数据集类别之一
            String className = FilenameUtils.getBaseName(classDirPath.toString());
            if (!classesNames.contains(className)) {
                continue;
            }

            // 列举类别子文件夹下的所有图片，比如 ['images/cat/1.jpg', 'images/cat/2.jpg', ...]
            File[] imagesPathList = classDirPath.listFiles();
            if (imagesPathList == null) {
                continue;
            }
            Arrays.sort(imagesPathList);

            // 遍历图片
            for (File imagePath : imagesPathList) {
                // 判别是否为图片文件
                if (!imagePath.isFile() || !FileUtil.isImageFile(imagePath.toString())) {
                    continue;
                }

                // 图片重命名
                String fullPrefixPath = FilenameUtils.getFullPath(imagePath.toString());
                String fileName = CommonUtil.generateUuid() + "." + FilenameUtils.getExtension(imagePath.toString());
                String newImagePath = Paths.get(fullPrefixPath, fileName).toString();
                boolean renameSuccess = imagePath.renameTo(new File(newImagePath));
                if (!renameSuccess) {
                    // 重命名失败，放弃上传该图片
                    continue;
                }

                // 上传图片与标注信息
                String datasetImagePath = StorageConstants.getDatasetImagePath(datasetDO.getUuid(), fileName);
                String datasetAnnotationPath = StorageConstants.getDatasetAnnotationPath(datasetDO.getUuid(), fileName);

                URL url = null;
                try {
                    url = storageService.uploadLocalFile(datasetImagePath, newImagePath);
                    storageService.write(datasetAnnotationPath, className);
                } catch (Exception e) {
                    continue;
                }

                ImageDO imageDO = new ImageDO();
                imageDO.setUploaderName(requestUsername);
                imageDO.setDatasetId(datasetId);
                imageDO.setFilename(fileName);
                imageDO.setAnnotated(true);
                imageDO.setAnnotation(className);
                imageDO.setUrl(url.toString());
                imageDO.setClusterNumber(null);
                // image表 插入行, 同时更新所属数据集的images_number++, 已经一起写在sql里面了
                imageMapper.insert(imageDO);
            }
        }
    }
}
