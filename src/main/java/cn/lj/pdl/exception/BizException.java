package cn.lj.pdl.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Getter
public class BizException extends RuntimeException {
    private String message;
    private Integer code;
    private HttpStatus httpStatus;

    public BizException(BizExceptionEnum bizExceptionEnum) {
        this.message = bizExceptionEnum.getMessage();
        this.code = bizExceptionEnum.getCode();
        this.httpStatus = bizExceptionEnum.getHttpStatus();
    }
}
