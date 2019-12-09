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

    public static String generateUuidStartWithAlphabet() {
        // K8S Service 命名规范
        // a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character,
        // and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?').
        String uuid;
        do {
            uuid = UUID.randomUUID().toString().replaceAll("-", "");;
        } while (!Character.isAlphabetic(uuid.charAt(0)));
        return uuid;
    }
}
