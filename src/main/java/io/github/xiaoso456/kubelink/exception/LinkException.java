package io.github.xiaoso456.kubelink.exception;

public class LinkException extends Exception{
    public LinkException() {
        super();
    }

    public LinkException(String message) {
        super(message);
    }

    public LinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkException(Throwable cause) {
        super(cause);
    }

    protected LinkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
