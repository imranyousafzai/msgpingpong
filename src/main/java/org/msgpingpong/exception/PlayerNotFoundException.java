package org.msgpingpong.exception;

import org.msgpingpong.core.Constants;

/**
 * Thrown when a referenced player cannot be found in the system.
 */
public final class PlayerNotFoundException extends GameException {

    public PlayerNotFoundException(String playerId) {
        super(Constants.PLAYER_NOT_FOUND + playerId);
    }
}
