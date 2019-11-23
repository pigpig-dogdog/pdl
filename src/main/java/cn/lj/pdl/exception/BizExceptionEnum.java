package cn.lj.pdl.exception;

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
     * 业务相关的异常枚举类
     */

    SUCCESS("成功", 0, HttpStatus.OK),
    UNDEFINED_ERROR("预期之外的服务端错误: ", -1, HttpStatus.INTERNAL_SERVER_ERROR),

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
    // 这个异常暂时不会用到，详见 pdl.security.JwtTokenFilter line51 的说明
    USER_NOT_LOGIN("用户未登录", 2002, HttpStatus.FORBIDDEN),
    USER_TOKEN_INVALID("token无效", 2003, HttpStatus.FORBIDDEN),
    USER_LOGIN_STATUS_EXPIRED("登录状态过期", 2004, HttpStatus.FORBIDDEN),
    USER_LOGIN_USERNAME_NOT_EXIST("用户不存在", 2005, HttpStatus.BAD_REQUEST),
    USER_LOGIN_PASSWORD_ERROR("密码错误", 2006, HttpStatus.BAD_REQUEST);

    private String message;
    private Integer code;
    private HttpStatus httpStatus;
}
