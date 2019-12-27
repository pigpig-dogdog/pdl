package cn.lj.pdl.constant;

/**
 * @author luojian
 * @date 2019/12/17
 */
public class K8sConstants {
    public static final String NAMESPACE_DEFAULT = "default";

    public static final String LABEL_APP = "app";
    public static final String LABEL_CREATOR = "creator";
    public static final String LABEL_ALGO_TRAIN_NAME = "algo_train_name";
    public static final String LABEL_ALGO_DEPLOY_NAME = "algo_deploy_name";
    public static final String LABEL_JOB_NAME = "job_name";

    public static final String PORT_NAME = "port-name";

    public static final String ALGO_TRAIN_IMAGE = "pdl-algo-train:0.1";
    public static final String ALGO_TRAIN_COMMAND = "python";
    public static final String ALGO_TRAIN_ENTRY = "main.py";

    public static final String ALGO_DEPLOY_IMAGE = "pdl-algo-deploy:0.1";
    public static final String ALGO_DEPLOY_COMMAND = "python";
    public static final String ALGO_DEPLOY_ENTRY = "main.py";
    public static int ALGO_DEPLOY_PORT = 5000;

}
