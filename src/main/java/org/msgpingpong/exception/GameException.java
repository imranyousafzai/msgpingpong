package org.msgpingpong.exception;

/**
 * Base sealed exception for all custom errors in the messaging game.
 * Provides a unified hierarchy for better error handling and logging.
 */
public sealed abstract class GameException extends RuntimeException
        permits CompilationException, CommunicationException, InvalidModeException, PlayerNotFoundException {

    protected GameException(String message) {
        super(message);
    }

    protected GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
