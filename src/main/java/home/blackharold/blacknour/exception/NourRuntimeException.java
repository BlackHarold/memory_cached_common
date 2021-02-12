package home.blackharold.blacknour.exception;

public class NourRuntimeException extends RuntimeException {
    public NourRuntimeException(String message) {
        super(message);
    }

    public NourRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NourRuntimeException(Throwable cause) {
        super(cause);
    }
}
