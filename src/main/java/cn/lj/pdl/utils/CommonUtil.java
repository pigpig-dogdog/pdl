package cn.lj.pdl.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
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
            uuid = UUID.randomUUID().toString();
        } while (!Character.isAlphabetic(uuid.charAt(0)));
        return uuid.replaceAll("-", "");
    }

    public static String encodeChinese(String s) {
        // 由于K8S Deployment 不支持标签带中文，所以要对中文进行编解码，同样适用于英文
        return Hex.encodeHexString(s.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeChinese(String s) throws DecoderException {
        // 由于K8S Deployment 不支持标签带中文，所以要对中文进行编解码，同样适用于英文
        return s == null ? null : new String(Hex.decodeHex(s), StandardCharsets.UTF_8);
    }
}
