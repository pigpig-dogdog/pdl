package cn.lj.pdl.constant;

/**
 * @author luojian
 * @date 2019/12/17
 */
public class K8sConstants {
    public static final String NAMESPACE_DEFAULT = "default";
    public static final String LABEL_APP = "app";
    public static final String PORT_NAME = "port-name";

    public static final String IMAGE_PULL_POLICY_ALWAYS = "Always";
    public static final String IMAGE_PULL_POLICY_IF_NOT_PRESENT = "IfNotPresent";
    public static final String IMAGE_PULL_POLICY_NEVER = "Never";

    private static final String ALGO_TRAIN_IMAGE_FORMAT = "ubuntu16.04-%s-%s-pdl_algo_train:latest";
    public static final String ALGO_TRAIN_COMMAND = "python";
    public static final String ALGO_TRAIN_ENTRY = "main.py";

    private static final String ALGO_DEPLOY_IMAGE_FORMAT = "ubuntu16.04-%s-%s-pdl_algo_deploy:latest";
    public static final String ALGO_DEPLOY_COMMAND = "python";
    public static final String ALGO_DEPLOY_ENTRY = "main.py";
    public static int ALGO_DEPLOY_PORT = 5000;

    public static String getAlgoTrainImageName(Language language, Framework framework) {
        return String.format(ALGO_TRAIN_IMAGE_FORMAT, language.getValue(), framework.getValue());
    }

    public static String getAlgoDeployImageName(Language language, Framework framework) {
        return String.format(ALGO_DEPLOY_IMAGE_FORMAT, language.getValue(), framework.getValue());
    }

}
