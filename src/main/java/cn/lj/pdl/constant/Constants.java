package cn.lj.pdl.constant;

import java.nio.file.Paths;

/**
 * @author luojian
 * @date 2019/12/18
 */
public class Constants {
    public static String TMP_FILE_UPLOAD_FOLDER = Paths.get(System.getProperty("user.dir"), "tmp_file_upload_folder").toString();
    public static int REPLICAS_MAX_VALUE = 10;

    public static String AUTO_ALGO_TASK_ALGOTRAIN_CREATOR_NAME = "system_auto_algo_task";
    public static Framework AUTO_ALGO_TASK_ALGOTRAIN_FRAMEWORK = Framework.MXNET;
    public static double AUTO_ALGO_TASK_DATASET_TRAIN_RATIO = 0.9;
    public static double AUTO_ALGO_TASK_DATASET_TEST_RATIO = 0.1;
    public static String[] CLASSIFICATION_MODELS = {"ResNet50_v1", "MobileNet1.0", "Xception"};
    public static String[] DETECTION_MODELS = {"faster_rcnn_resnet50_v1b_voc", "ssd_300_vgg16_atrous_voc", "yolo3_darknet53_voc"};
}
