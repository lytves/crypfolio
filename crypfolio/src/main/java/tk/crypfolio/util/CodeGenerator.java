package tk.crypfolio.util;

import java.util.UUID;

/**
 * A utility class to generate UUID codes to send for user in cases of email verification or reset password
 */
public abstract class CodeGenerator {

    public static String generateCodeUUID(){

        return UUID.randomUUID().toString();
    }
}