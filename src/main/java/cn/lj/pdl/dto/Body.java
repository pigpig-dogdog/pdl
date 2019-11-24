package cn.lj.pdl.dto;

import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Data
@AllArgsConstructor
public class Body<T> {
    private Boolean success;
    private String message;
    private Integer code;
    private T data;

    public static <T> Body<T> buildSuccess(T data) {
        return new Body<>(
                true,
                BizExceptionEnum.SUCCESS.getMessage(),
                BizExceptionEnum.SUCCESS.getCode(),
                data);
    }

    public static Body buildFail(BizException e) {
        return new Body<>(false, e.getMessage(), e.getCode(), null);
    }

    public static Body buildFail(BizExceptionEnum e) {
        return new Body<>(false, e.getMessage(), e.getCode(), null);
    }

    public Body appendMessage(String appendMessage) {
        this.setMessage(this.getMessage() + appendMessage);
        return this;
    }
}
