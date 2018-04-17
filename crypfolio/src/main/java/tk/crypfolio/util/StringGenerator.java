package tk.crypfolio.util;

public class StringGenerator {

    private static final String NUMBERS = "0123456789";
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";

    private static String setString(int length) {
        return setString(NUMBERS + UPPER_CASE + LOWER_CASE, length);
    }

    private static String setString(String key, int length) {
        StringBuilder string = new StringBuilder();

        for (int i = 0; i < length; i++) {
            string.append(key.charAt((int) (Math.random() * key.length())));
        }

        return string.toString();
    }

    public static String setStringNumeric(int length) {
        return setString(NUMBERS, length);
    }

    public static String setStringAlphanumeric(int length) {
        return setString(length);
    }


}
