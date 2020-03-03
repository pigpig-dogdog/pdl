package cn.lj.pdl.runnable;

import cn.lj.pdl.constant.Constants;
import cn.lj.pdl.constant.StorageConstants;
import cn.lj.pdl.dto.dataset.annotation.DetectionBbox;
import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.mapper.DatasetMapper;
import cn.lj.pdl.mapper.ImageMapper;
import cn.lj.pdl.model.DatasetDO;
import cn.lj.pdl.model.ImageDO;
import cn.lj.pdl.service.StorageService;
import cn.lj.pdl.utils.CommonUtil;
import cn.lj.pdl.utils.FileUtil;
import cn.lj.pdl.utils.PascalVocXmlParser;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
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
    private int uploadType;
    private DatasetDO datasetDO;
    private List<String> classesNames;

    public UploadImagesZipRunnable(DatasetMapper datasetMapper,
                                   ImageMapper imageMapper,
                                   StorageService storageService,
                                   Long datasetId,
                                   String zipFilePath,
                                   String requestUsername,
                                   int uploadType) {
        this.datasetMapper = datasetMapper;
        this.imageMapper = imageMapper;
        this.storageService = storageService;
        this.datasetId = datasetId;
        this.zipFilePath = zipFilePath;
        this.requestUsername = requestUsername;
        this.uploadType = uploadType;

        datasetDO = datasetMapper.findById(datasetId);
        if (datasetDO == null) {
            throw new BizException(BizExceptionEnum.DATASET_NOT_EXIST);
        }
        classesNames = Arrays.asList(datasetDO.getClassesNames().split("\\s+"));
        log.info("classes_names:{}", classesNames);
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
        String dataDirPath = Paths.get(unzipDirPath, "data").toString();
        if (Files.exists(Paths.get(dataDirPath))) {
            uploadData(dataDirPath);
        } else {
            log.error("Directory 'data/' not found in directory '{}'.", unzipDirPath);
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

    private void uploadData(String dataDirPath) {
        if (uploadType == Constants.UPLOAD_TYPE_UNANNOTATED) {
            uploadUnAnnotatedImages(dataDirPath);
        } else if (uploadType == Constants.UPLOAD_TYPE_CLASSIFICATION) {
            uploadClassificationImages(dataDirPath);
        } else if (uploadType == Constants.UPLOAD_TYPE_DETECTION) {
            uploadDetectionImages(dataDirPath);
        } else {
            log.error(String.format("unknown upload type: %d", uploadType));
        }
    }

    private void uploadUnAnnotatedImages(String dataDirPath) {
        Iterator it = FileUtils.iterateFiles(new File(dataDirPath), null, true);
        while(it.hasNext()) {
            File file = (File) it.next();
            handleImageFile(file, null);
        }
    }

    private void uploadClassificationImages(String dataDirPath) {
        // 列举类别子文件夹，比如 ['data/cat/', 'data/dog/', ...]
        File[] classesDirList = new File(dataDirPath).listFiles();
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

            // 列举类别子文件夹下的所有图片，比如 ['data/cat/1.jpg', 'data/cat/2.jpg', ...]
            File[] imagesFileList = classDirPath.listFiles();
            if (imagesFileList == null) {
                continue;
            }
            Arrays.sort(imagesFileList);

            // 遍历图片
            for (File imagePath : imagesFileList) {
                handleImageFile(imagePath, className);
            }
        }
    }

    private void uploadDetectionImages(String dataDirPath) {
        String annotationDirPath = Paths.get(dataDirPath, "annotation").toString();
        String imagesDirPath = Paths.get(dataDirPath, "image").toString();
        if (!Files.exists(Paths.get(annotationDirPath))) {
            log.error(String.format("annotation dir path: '%s' not exist!", annotationDirPath));
            return;
        }
        if (!Files.exists(Paths.get(imagesDirPath))) {
            log.error(String.format("images dir path: '%s' not exist!", imagesDirPath));
            return;
        }

        // 列举 'images/annotation' 文件夹
        File[] annotationFileList = new File(annotationDirPath).listFiles();
        if (annotationFileList == null) {
            return;
        }
        for (File annotationFile : annotationFileList) {
            if (!annotationFile.toString().endsWith(".xml")) {
                continue;
            }
            try {
                Pair<String, List<DetectionBbox>> parseResult = PascalVocXmlParser.parse(annotationFile.toString());
                String imageFilename = parseResult.getLeft();
                List<DetectionBbox> bboxes = parseResult.getRight();
                File imagePath = Paths.get(imagesDirPath, imageFilename).toFile();
                handleImageFile(imagePath, JSON.toJSONString(bboxes));
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.toString());
            }
        }
    }

    private void handleImageFile(File imageFile, String annotation) {
        // 判别是否为图片文件
        if (!imageFile.isFile() || !FileUtil.isImageFile(imageFile.toString())) {
            return;
        }

        // 图片重命名
        String fullPrefixPath = FilenameUtils.getFullPath(imageFile.toString());
        String fileName = CommonUtil.generateUuid() + "." + FilenameUtils.getExtension(imageFile.toString());
        String newImagePath = Paths.get(fullPrefixPath, fileName).toString();
        boolean renameSuccess = imageFile.renameTo(new File(newImagePath));
        if (!renameSuccess) {
            // 重命名失败，放弃上传该图片
            log.error("rename error");
            return;
        }

        // 上传图片与标注信息
        String datasetImagePath = StorageConstants.getDatasetImagePath(datasetDO.getUuid(), fileName);
        String datasetAnnotationPath = StorageConstants.getDatasetAnnotationPath(datasetDO.getUuid(), fileName);

        URL url = null;
        try {
            url = storageService.uploadLocalFile(datasetImagePath, newImagePath);
            if (annotation != null) {
                storageService.write(datasetAnnotationPath, annotation);
            }
        } catch (Exception e) {
            log.error(e.toString());
            return;
        }

        ImageDO imageDO = new ImageDO();
        imageDO.setUploaderName(requestUsername);
        imageDO.setDatasetId(datasetId);
        imageDO.setFilename(fileName);
        imageDO.setAnnotated(annotation != null);
        imageDO.setAnnotation(annotation);
        imageDO.setUrl(url.toString());
        imageDO.setClusterNumber(null);
        imageDO.setPredictClassName(null);
        // image表 插入行, 同时更新所属数据集的images_number++, 已经一起写在sql里面了
        imageMapper.insert(imageDO);
    }
}
