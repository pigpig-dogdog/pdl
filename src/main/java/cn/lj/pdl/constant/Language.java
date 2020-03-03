package cn.lj.pdl.constant;

/**
 * @author luojian
 * @date 2020/1/1
 */
public enum Language {
    /**
     * python 2.7
     */
    PYTHON_2_7("py2.7"),

    /**
     * python 3.6
     */
    PYTHON_3_6("py3.6"),

    ;

    private String value;
    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
