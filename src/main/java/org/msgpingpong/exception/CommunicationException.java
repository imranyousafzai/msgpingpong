package org.msgpingpong.exception;

/**
 * Thrown when message delivery or socket communication fails.
 */
public final class CommunicationException extends GameException {
    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
