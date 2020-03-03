package cn.lj.pdl.constant;

/**
 * @author luojian
 * @date 2019/11/30
 */
public enum Framework {
    /**
     * Tensorflow
     */
    TENSORFLOW("仅用于列表搜索"),
    TENSORFLOW_1_14_0("tensorflow1.14.0"),
    TENSORFLOW_1_15_0("tensorflow1.15.0"),
    TENSORFLOW_2_0_0("tensorflow2.0.0"),
    /**
     * Keras
     */
    KERAS("仅用于列表搜索"),
    KERAS_2_3_0("keras2.3.0"),
    KERAS_2_3_1("keras2.3.1"),

    /**
     * PyTorch
     */
    PYTORCH("仅用于列表搜索"),
    PYTORCH_1_3_1("pytorch1.3.1"),

    MXNET("todo要删"),
    CAFFE("todo要删"),
    ;

    private String value;
    Framework(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
