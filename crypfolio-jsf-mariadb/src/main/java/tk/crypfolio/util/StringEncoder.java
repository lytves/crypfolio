package tk.crypfolio.util;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class StringEncoder {

    public static String encodePassword(String email, String password){

        return DigestUtils.sha256Hex(email + "-" + password);
    }
}
