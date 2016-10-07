package toni.forth;

public class ForthException extends RuntimeException {

    public ForthException() {
    }

    public ForthException(String message) {
        super(message);
    }

    public ForthException(Throwable cause) {
        super(cause);
    }

    public ForthException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForthException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
