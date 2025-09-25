package org.msgpingpong.exception;

import org.msgpingpong.core.Constants;

/**
 * Thrown when an invalid run mode is provided (e.g., not single/server/client).
 */
public final class InvalidModeException extends GameException {

    public InvalidModeException(String mode) {
        super(Constants.INVALID_MODE + mode + Constants.USE_SINGLE_SERVER_OR_CLIENT);
    }
}
