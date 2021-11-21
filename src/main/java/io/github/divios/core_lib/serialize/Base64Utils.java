package io.github.divios.core_lib.serialize;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Base64Utils {

    public static String toBase64(byte[] b) {
        return Base64Coder.encodeLines(b);
    }

    public static String toBase64(String s) {
        return toBase64(s.getBytes());
    }

    public static String fromBase64(String s) {
        return Base64Coder.decodeString(s);
    }

}
