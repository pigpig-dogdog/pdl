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

    private final static String ALGOTRAIN_ROOT_PATH = "algo-train/";
    private final static String ALGOTRAIN_DIR_PATH_FORMAT = "algo-train/%s/";
    private final static String ALGOTRAIN_CODE_ZIP_FILE_PATH_FORMAT = "algo-train/%s/code.zip";
    private final static String ALGOTRAIN_STATUS_FILE_PATH_FORMAT = "algo-train/%s/status.txt";
    private final static String ALGOTRAIN_RESULT_ZIP_FILE_PATH_FORMAT = "algo-train/%s/result.zip";

    private final static String ALGODEPLOY_ROOT_PATH = "algo-deploy/";
    private final static String ALGODEPLOY_DIR_PATH_FORMAT = "algo-deploy/%s/";
    private final static String ALGODEPLOY_CODE_ZIP_FILE_PATH_FORMAT = "algo-deploy/%s/code.zip";
    private final static String ALGODEPLOY_STATUS_FILE_PATH_FORMAT = "algo-deploy/%s/status.txt";

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

    public static String getAlgoTrainRootPath() {
        return ALGOTRAIN_ROOT_PATH;
    }

    public static String getAlgoTrainDirPath(String algoTrainUuid) {
        return String.format(ALGOTRAIN_DIR_PATH_FORMAT, algoTrainUuid);
    }

    public static String getAlgoTrainCodeZipFilePath(String algoTrainUuid) {
        return String.format(ALGOTRAIN_CODE_ZIP_FILE_PATH_FORMAT, algoTrainUuid);
    }

    public static String getAlgoTrainStatusFilePath(String algoTrainUuid) {
        return String.format(ALGOTRAIN_STATUS_FILE_PATH_FORMAT, algoTrainUuid);
    }

    public static String getAlgoTrainResultZipFilePath(String algoTrainUuid) {
        return String.format(ALGOTRAIN_RESULT_ZIP_FILE_PATH_FORMAT, algoTrainUuid);
    }

    public static String getAlgoDeployRootPath() {
        return ALGODEPLOY_ROOT_PATH;
    }

    public static String getAlgoDeployDirPath(String algoDeployUuid) {
        return String.format(ALGODEPLOY_DIR_PATH_FORMAT, algoDeployUuid);
    }

    public static String getAlgoDeployCodeZipFilePath(String algoDeployUuid) {
        return String.format(ALGODEPLOY_CODE_ZIP_FILE_PATH_FORMAT, algoDeployUuid);
    }

    public static String getAlgoDeployStatusFilePath(String algoDeployUuid) {
        return String.format(ALGODEPLOY_STATUS_FILE_PATH_FORMAT, algoDeployUuid);
    }

}
