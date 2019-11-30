package cn.lj.pdl.constant;

/**
 * @author luojian
 * @date 2019/11/25
 */
public class Constants {
    public final static String SYMBOL_SLASH = "/";
    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static int CLASSIFICATION_MIN_CLASSES_NUMBER = 2;
    public final static int DETECTION_MIN_CLASSES_NUMBER = 1;

    /**
     * 存储服务文件树
     */
    private final static String DATASET_ROOT_PATH = "datasets/";
    private final static String DATASET_DIR_PATH_FORMAT = "datasets/%s/";
    private final static String DATASET_COVER_IMAGE_PATH_FORMAT = "datasets/%s/cover.%s";
    private final static String DATASET_IMAGES_DIR_PATH_FORMAT = "datasets/%s/images/";
    private final static String DATASET_ANNOTATIONS_DIR_PATH_FORMAT = "datasets/%s/annotations/";
    private final static String DATASET_IMAGE_PATH_FORMAT = "datasets/%s/images/%s";
    private final static String DATASET_ANNOTATION_PATH_FORMAT = "datasets/%s/annotations/%s.xml";

    public static String getDatasetRootPath() {
        return DATASET_ROOT_PATH;
    }

    public static String getDatasetDirPath(String datasetUuid) {
        return String.format(DATASET_DIR_PATH_FORMAT, datasetUuid);
    }

    public static String getDatasetCoverImagePath(String datasetUuid, String extension) {
        return String.format(DATASET_COVER_IMAGE_PATH_FORMAT, datasetUuid, extension);
    }

    public static String getDatasetImagesDirPath(String datasetUuid) {
        return String.format(DATASET_IMAGES_DIR_PATH_FORMAT, datasetUuid);
    }

    public static String getDatasetAnnotationsDirPath(String datasetUuid) {
        return String.format(DATASET_ANNOTATIONS_DIR_PATH_FORMAT, datasetUuid);
    }

    public static String getDatasetImagePath(String datasetUuid, String fileName) {
        return String.format(DATASET_IMAGE_PATH_FORMAT, datasetUuid, fileName);
    }

    public static String getDatasetAnnotationPath(String datasetUuid, String fileName) {
        return String.format(DATASET_ANNOTATION_PATH_FORMAT, datasetUuid, fileName);
    }

}
