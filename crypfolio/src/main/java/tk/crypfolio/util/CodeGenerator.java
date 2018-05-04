package tk.crypfolio.util;

import java.util.UUID;

public abstract class CodeGenerator {

    public static String generateCodeUUID(){

        return UUID.randomUUID().toString();
    }
}
