package org.msgpingpong.comm;

import org.msgpingpong.model.Message;
import org.msgpingpong.model.Player;

/**
 * This sealed interface restricts implementations to known, validated types,
 * ensuring controlled communication strategies within the system.
 *
 * <p><b>Purpose:</b> To abstract the communication mechanism between players,
 * allowing for different implementations (in-memory vs. socket-based) while
 * maintaining a consistent API for message passing.
 *
 * <p><b>Implementations:</b>
 * <ul>
 *   <li>{@link InMemoryCommunication} - For single-process, in-memory message passing</li>
 *   <li>{@link SocketCommunication} - For multi-process communication via TCP sockets</li>
 * </ul>
 *
 * @see InMemoryCommunication
 * @see SocketCommunication
 * @see Player
 * @see Message
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public sealed interface CommunicationStrategy
        permits InMemoryCommunication, SocketCommunication {

    /**
     * Sends a message from one player to another using the implemented strategy.
     *
     * @param from the player sending the message (cannot be null)
     * @param to the recipient player (cannot be null)
     * @param msg the message content to be sent (cannot be null)
     * @throws IllegalArgumentException if any parameter is null
     * @throws RuntimeException if message delivery fails (implementation-specific)
     */
    void sendMessage(Player from, Player to, Message msg);

    /**
     * Retrieves a player by their unique identifier.
     *
     * @param playerId the unique identifier of the player to find
     * @return the player with the specified ID, or null if not found
     * @throws IllegalArgumentException if playerId is null or empty
     */
    Player findPlayer(String playerId);
}

