package cn.lj.pdl.constant;

/**
 * @author luojian
 * @date 2019/11/25
 */
public class StorageConstants {
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
    private final static String ALGOTRAIN_CODE_ZIP_FILE_PATH_FORMAT = "algo-train/%s/%s";
    private final static String ALGOTRAIN_RESULT_ZIP_FILE_PATH_FORMAT = "algo-train/%s/result.zip";
    private final static String ALGOTRAIN_LOG_PATH_FORMAT = "algo-train/%s/log.txt";

    private final static String ALGODEPLOY_ROOT_PATH = "algo-deploy/";
    private final static String ALGODEPLOY_DIR_PATH_FORMAT = "algo-deploy/%s/";
    private final static String ALGODEPLOY_CODE_ZIP_FILE_PATH_FORMAT = "algo-deploy/%s/%s";

    private final static String AUTOALGOTASK_ROOT_PATH = "auto-algo-task/";
    private final static String AUTOALGOTASK_DIR_PATH_FORMAT = "auto-algo-task/%s/";
    private final static String AUTOALGOTASK_DATASET_TRAIN_LIST_FORMAT = "auto-algo-task/%s/train.txt";
    private final static String AUTOALGOTASK_DATASET_TEST_LIST_FORMAT = "auto-algo-task/%s/test.txt";
    private final static String AUTOALGOTASK_ALGOTRAIN_CODE_ZIP_FILE_PATH = "auto-algo-task/auto_algo_task.zip";
    private final static String AUTOALGOTASK_ALGOTRAIN_ACCURACY_FORMAT = "auto-algo-task/%s/accuracy-%s.txt";
    /**
     * 训练程序结束之后，将会把整个代码文件上传（与原来相比多了训练模型）
     * 该代码文件就作为部署的代码
     */
    private final static String AUTOALGOTASK_ALGODEPLOY_CODE_ZIP_FILE_PATH_FORMAT = "auto-algo-task/%s/deploy-code-%s.zip";

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

    public static String getAlgoTrainCodeZipFilePath(String algoTrainUuid, String filename) {
        return String.format(ALGOTRAIN_CODE_ZIP_FILE_PATH_FORMAT, algoTrainUuid, filename);
    }

    public static String getAlgoTrainResultZipFilePath(String algoTrainUuid) {
        return String.format(ALGOTRAIN_RESULT_ZIP_FILE_PATH_FORMAT, algoTrainUuid);
    }

    public static String getAlgoTrainLogPath(String algoTrainUuid) {
        return String.format(ALGOTRAIN_LOG_PATH_FORMAT, algoTrainUuid);
    }

    public static String getAlgoDeployRootPath() {
        return ALGODEPLOY_ROOT_PATH;
    }

    public static String getAlgoDeployDirPath(String algoDeployUuid) {
        return String.format(ALGODEPLOY_DIR_PATH_FORMAT, algoDeployUuid);
    }

    public static String getAlgoDeployCodeZipFilePath(String algoDeployUuid, String filename) {
        return String.format(ALGODEPLOY_CODE_ZIP_FILE_PATH_FORMAT, algoDeployUuid, filename);
    }

    public static String getAutoAlgoTaskRootPath() {
        return AUTOALGOTASK_ROOT_PATH;
    }

    public static String getAutoAlgoTaskDirPath(String autoAlgoTaskUuid) {
        return String.format(AUTOALGOTASK_DIR_PATH_FORMAT, autoAlgoTaskUuid);
    }

    public static String getAutoAlgoTaskDatasetTrainList(String autoAlgoTaskUuid) {
        return String.format(AUTOALGOTASK_DATASET_TRAIN_LIST_FORMAT, autoAlgoTaskUuid);
    }

    public static String getAutoAlgoTaskDatasetTestList(String autoAlgoTaskUuid) {
        return String.format(AUTOALGOTASK_DATASET_TEST_LIST_FORMAT, autoAlgoTaskUuid);
    }

    public static String getAutoAlgoTaskAlgoTrainCodeZipFilePath() {
        return AUTOALGOTASK_ALGOTRAIN_CODE_ZIP_FILE_PATH;
    }

    public static String getAutoAlgoTaskAlgoTrainAccuracy(String autoAlgoTaskUuid, String algoTrainUuid) {
        return String.format(AUTOALGOTASK_ALGOTRAIN_ACCURACY_FORMAT, autoAlgoTaskUuid, algoTrainUuid);
    }

    public static String getAutoAlgoTaskAlgoDeployCodeZipFilePath(String autoAlgoTaskUuid, String algoTrainUuid) {
        return String.format(AUTOALGOTASK_ALGODEPLOY_CODE_ZIP_FILE_PATH_FORMAT, autoAlgoTaskUuid, algoTrainUuid);
    }

}
