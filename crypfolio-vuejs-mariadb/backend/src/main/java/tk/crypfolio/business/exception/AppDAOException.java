package tk.crypfolio.business.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
* Handling Dao exceptions in service layer
* https://stackoverflow.com/a/9256648/6841308
* */
public class AppDAOException extends Exception {

    private static final Logger LOGGER = Logger.getLogger(AppDAOException.class.getName());

    public final static int _FAIL_TO_INSERT = 1;
    public final static int _UPDATE_FAILED = 2;
    public final static int _SQL_ERROR = 3;

    private int errorCode;

    public AppDAOException(String message, Throwable cause, int errorCode) {
        this.errorCode = errorCode;

        LOGGER.log(Level.WARNING, message + ", errorCode = " + errorCode);
        LOGGER.log(Level.WARNING, cause.getMessage());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}