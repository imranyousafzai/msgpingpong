package org.msgpingpong.exception;

/**
 * Thrown when Java source compilation fails at runtime.
 */
public final class CompilationException extends GameException {
    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}
