package cn.lj.pdl.utils;

import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import cn.lj.pdl.dto.Body;
import org.springframework.http.ResponseEntity;

/**
 * @author luojian
 * @date 2019/11/23
 */
public class ResponseEntityUtil {

    public static ResponseEntity<Body> buildFail(BizException e) {
        Body body = Body.buildFail(e);
        return ResponseEntity.status(e.getHttpStatus()).body(body);
    }

    public static ResponseEntity<Body> buildFail(BizExceptionEnum e) {
        Body body = Body.buildFail(e);
        return ResponseEntity.status(e.getHttpStatus()).body(body);
    }

    public static ResponseEntity<Body> buildFail(BizException e, String appendMessage) {
        Body body = Body.buildFail(e).appendMessage(appendMessage);
        return ResponseEntity.status(e.getHttpStatus()).body(body);
    }

    public static ResponseEntity<Body> buildFail(BizExceptionEnum e, String appendMessage) {
        Body body = Body.buildFail(e).appendMessage(appendMessage);
        return ResponseEntity.status(e.getHttpStatus()).body(body);
    }

}
