package cn.lj.pdl.exception;

import cn.lj.pdl.model.Body;
import cn.lj.pdl.utils.ResponseEntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author luojian
 * @date 2019/11/23
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /*
     * 注：package cn.lj.pdl.security.JwtTokenFilter 处理登录相关的异常
     */

    /**
     * Controller层 @RequestParam注解抛出 参数缺失异常
     * 注解 @RequestParam 等价于 @RequestParam(required = true)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Body> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntityUtil.buildFail(BizExceptionEnum.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION, e.getMessage());
    }

    /**
     * Controller层 @Valid注解抛出 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Body> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 汇总错误信息
        StringBuilder summary = new StringBuilder();
        for (ObjectError err : e.getBindingResult().getAllErrors()) {
            summary.append(err.getDefaultMessage()).append(", ");
        }
        summary.setLength(summary.length() - 2);
        return ResponseEntityUtil.buildFail(BizExceptionEnum.METHOD_ARGUMENT_NOT_VALID_EXCEPTION, summary.toString());
    }

    /**
     * 业务相关的异常
     */
    @ExceptionHandler(value = BizException.class)
    public ResponseEntity<Body> handleBizException(BizException e) {
        return ResponseEntityUtil.buildFail(e);
    }

    /**
     * 未定义的异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Body> handleException(Exception e) {
        log.error(e.toString());
        return ResponseEntityUtil.buildFail(BizExceptionEnum.UNDEFINED_ERROR, e.getMessage());
    }
}