package cn.lj.pdl.constant;

import java.nio.file.Paths;

/**
 * @author luojian
 * @date 2019/12/18
 */
public class Constants {
    public static String TMP_FILE_UPLOAD_FOLDER = Paths.get(System.getProperty("user.dir"), "tmp_file_upload_folder").toString();
    public static int UPLOAD_TYPE_UNANNOTATED = 0;
    public static int UPLOAD_TYPE_CLASSIFICATION = 1;
    public static int UPLOAD_TYPE_DETECTION = 2;

    public static int REPLICAS_MAX_VALUE = 10;

    public static Language AUTO_ALGO_TASK_ALGOTRAIN_LANGUAGE = Language.PYTHON_3_6;
    public static Framework AUTO_ALGO_TASK_ALGOTRAIN_FRAMEWORK = Framework.KERAS_2_3_1;
    public static String AUTO_ALGO_TASK_ALGODEPLOY_MAIN_CLASS_PATH = "deploy.AlgorithmModel";
    public static Integer AUTO_ALGO_TASK_ALGOTRAIN_MIN_ANNOTATED_IMAGES_NUMBER = 100;
    public static double AUTO_ALGO_TASK_DATASET_TRAIN_RATIO = 0.9;
    public static double AUTO_ALGO_TASK_DATASET_TEST_RATIO = 0.1;
    public static String[] CLASSIFICATION_MODELS = {"MobileNet", "ResNet50", "DenseNet121"};
    public static String[] DETECTION_MODELS = {"ssd_inception_v2_coco", "faster_rcnn_resnet50_coco", "rfcn_resnet101_coco"};


    public static Language IMAGE_CLUSTER_TASK_ALGOTRAIN_LANGUAGE = Language.PYTHON_3_6;
    public static Framework IMAGE_CLUSTER_TASK_ALGOTRAIN_FRAMEWORK = Framework.KERAS_2_3_1;
}
