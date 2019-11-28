package cn.lj.pdl.utils;

import java.util.UUID;

/**
 * @author luojian
 * @date 2019/11/28
 */
public class CommonUtil {

    public static String generateUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
