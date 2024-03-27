package io.github.xiaoso456.kubelink.exception.runtime;

public class LinkRuntimeException extends RuntimeException{
    public LinkRuntimeException() {
        super();
    }

    public LinkRuntimeException(String message) {
        super(message);
    }

    public LinkRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkRuntimeException(Throwable cause) {
        super(cause);
    }

    protected LinkRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
