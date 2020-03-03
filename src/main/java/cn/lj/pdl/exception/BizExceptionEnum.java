package cn.lj.pdl.exception;

import cn.lj.pdl.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Getter
@AllArgsConstructor
public enum BizExceptionEnum {
    /**
     * 成功
     */
    SUCCESS("成功", 0, HttpStatus.OK),

    /**
     * 预期之外的服务端错误
     */
    UNEXPECTED_SERVER_ERROR("预期之外的服务端错误: ", -1, HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * Controller层相关
     */
    // @RequestParam注解抛出的参数缺失异常（具体错误原因会在统一异常处理时填写)
    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION("参数缺失错误: ", 1001, HttpStatus.BAD_REQUEST),
    // @Valid注解抛出的参数校验异常（具体错误原因会在统一异常处理时填写）
    METHOD_ARGUMENT_NOT_VALID_EXCEPTION("参数校验错误: ", 1002, HttpStatus.BAD_REQUEST),

    /**
     * 用户相关
     */
    USER_REGISTER_USERNAME_EXIST("用户名已存在", 2001, HttpStatus.BAD_REQUEST),
    // USER_NOT_LOGIN 这个异常暂时不会用到，详见 pdl.security.JwtTokenFilter line51 的说明
    USER_NOT_LOGIN("用户未登录", 2002, HttpStatus.FORBIDDEN),
    USER_TOKEN_INVALID("token无效", 2003, HttpStatus.FORBIDDEN),
    USER_LOGIN_STATUS_EXPIRED("登录状态过期", 2004, HttpStatus.FORBIDDEN),
    USER_LOGIN_USERNAME_NOT_EXIST("用户不存在", 2005, HttpStatus.BAD_REQUEST),
    USER_LOGIN_PASSWORD_ERROR("密码错误", 2006, HttpStatus.BAD_REQUEST),
    USER_IS_NOT_CREATOR("非创建者无权操作", 2007, HttpStatus.BAD_REQUEST),

    /**
     * 数据集相关
     */
    DATASET_NAME_EXIST("数据集名称已存在", 3001, HttpStatus.BAD_REQUEST),
    ALGO_TYPE_ERROR("算法类别必须是['CLASSIFICATION', 'DETECTION']其中之一", 3002, HttpStatus.BAD_REQUEST),
    CLASSIFICATION_CLASSES_NUMBER_LESS_THAN_TWO("图像分类任务类别数目必须大于等于2", 3003, HttpStatus.BAD_REQUEST),
    DETECTION_CLASSES_NUMBER_LESS_THAN_ONE("目标检测任务类别数目必须大于等于1", 3004, HttpStatus.BAD_REQUEST),
    CLASSES_NUMBER_NOT_EQUAL_TO_CLASSES_NAME_LIST_SIZE("类别数目不等于类名数目", 3005, HttpStatus.BAD_REQUEST),
    CLASSES_NAME_LIST_REPEAT("类名有重复", 3006, HttpStatus.BAD_REQUEST),
    DATASET_NOT_EXIST("数据集不存在", 3007, HttpStatus.BAD_REQUEST),
    IMAGE_NOT_EXIST("图片不存在", 3008, HttpStatus.BAD_REQUEST),
    IMAGE_NOT_BELONG_TO_THIS_DATASET("图片不属于该数据集", 3009, HttpStatus.BAD_REQUEST),
    DATASET_ALGO_TYPE_IS_NOT_CLASSIFICATION("仅分类任务可以进行图像聚类", 3010, HttpStatus.BAD_REQUEST),
    UNKNOWN_DATA_UPLOAD_TYPE("未知的数据上传类型", 3011, HttpStatus.BAD_REQUEST),

    /**
     * 文件上传
     */
    EMPTY_FILE("文件不可为空", 4001, HttpStatus.BAD_REQUEST),
    NOT_IMAGE_FILE("不是有效的图片文件", 4002, HttpStatus.BAD_REQUEST),
    NOT_ZIP_FILE("不是zip压缩文件", 4003, HttpStatus.BAD_REQUEST),
    FILE_SIZE_TOO_LARGE("文件大小不可超过10MB", 4004, HttpStatus.BAD_REQUEST),
    UPLOAD_MULTIPART_FILE_EXCEPTION("文件上传异常: ", 4005, HttpStatus.INTERNAL_SERVER_ERROR),
    DIR_NAME_NOT_ENDSWITH_SLASH("目录名不是以反斜杠结尾", 4006, HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NAME_ENDSWITH_SLASH("文件名以反斜杠结尾", 4007, HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * 算法部署（算法在线化服务）相关
     */
    ALGO_DEPLOY_NOT_EXIST("算法在线化服务不存在", 5001, HttpStatus.BAD_REQUEST),
    ALGO_DEPLOY_ALREADY_IN_EXITED("算法在线化服务已处于退出状态中", 5002, HttpStatus.BAD_REQUEST),
    ALGO_DEPLOY_ALREADY_IN_SERVING("算法在线化服务已处于服务状态中", 5003, HttpStatus.BAD_REQUEST),
    REPLICAS_CAN_NOT_BE_NULL("弹性伸缩的replicas不能是null", 5004, HttpStatus.BAD_REQUEST),
    REPLICAS_LESS_THEN_ONE("replicas不能小于1", 5005, HttpStatus.BAD_REQUEST),
    REPLICAS_GREATER_THEN_MAX_VALUE("replicas不能大于"+Constants.REPLICAS_MAX_VALUE, 5006, HttpStatus.BAD_REQUEST),

    /**
     * 自助式算法任务相关
     */
    AUTO_ALGO_TASK_NOT_EXIST("自助式算法任务不存在", 6001, HttpStatus.BAD_REQUEST),
    AUTO_ALGO_TASK_TRAIN_TEST_RATIO_SUM_NOT_EQUAL_TO_ONE("训练集与测试集比例之和不为1", 6002, HttpStatus.BAD_REQUEST),
    AUTO_ALGO_TASK_DATASET_ANNOTATED_NUMBER_TOO_LESS("已标注的图片数量过少，不建议开启自助式算法任务", 6003, HttpStatus.BAD_REQUEST)
    ;

    private String message;
    private Integer code;
    private HttpStatus httpStatus;
}
