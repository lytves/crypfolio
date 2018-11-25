package tk.crypfolio.common;

import java.math.MathContext;
import java.math.RoundingMode;

public abstract class Settings {

    public static final Integer EMAIL_VERIFICATION_LINK_TIMELIFE_SECONDS = 1800;

    public static final Integer PASSWORD_RESTORE_LINK_TIMELIFE_SECONDS = 1800;

    public static final String ADMIN_EMAIL = "PUT_YOUR_GMAIL_ID_HERE";

    public static final String ADMIN_EMAIL_PASSWORD = "PUT_YOUR_GMAIL_PASSWORD_HERE";

    public static final MathContext MATH_CONTEXT_8_PRECISION = new MathContext(8, RoundingMode.HALF_UP);

}
