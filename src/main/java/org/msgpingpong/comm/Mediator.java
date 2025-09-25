package org.msgpingpong.comm;

import org.msgpingpong.core.Constants;
import org.msgpingpong.exception.ExceptionHandler;
import org.msgpingpong.exception.PlayerNotFoundException;
import org.msgpingpong.model.Message;
import org.msgpingpong.model.Player;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Mediator coordinates communication between multiple Players
 * in single-process (in-memory) mode.
 *
 * <p>This class serves as the central hub for message routing in the in-memory
 * communication strategy, eliminating direct dependencies between Player objects.
 * It maintains a registry of all participating players and facilitates
 * message delivery between them.</p>
 *
 * <p><b>Thread Safety:</b> This implementation is not thread-safe. Concurrent
 * access to the player registry and message delivery should be synchronized
 * externally if used in multi-threaded environments.</p>
 *
 * @see InMemoryCommunication
 * @see Player
 * @see Message
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class Mediator {
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final ReadWriteLock registryLock = new ReentrantReadWriteLock();

    /**
     * Registers a player with the mediator for message routing.
     *
     * @param player the Player instance to be registered. Must not be null.
     * @throws IllegalArgumentException if the provided player is null.
     */
    public void register(Player player) {
        validatePlayer(player);
        registryLock.writeLock().lock();
        try {
            players.put(player.id(), player);
        } finally {
            registryLock.writeLock().unlock();
        }
    }

    /**
     * Finds a registered player by their unique identifier.
     *
     * @param playerId the unique identifier of the player to find
     * @return an Optional containing the found Player, or empty if no player
     *         with the given ID is registered
     * @throws IllegalArgumentException if the provided ID is null or empty
     */
    public Optional<Player> find(String playerId) {
        validatePlayerId(playerId);
        registryLock.readLock().lock();
        try {
            return Optional.ofNullable(players.get(playerId));
        } finally {
            registryLock.readLock().unlock();
        }
    }

    /**
     * Delivers a message from one player to another.
     *
     * <p>This method immediately forwards the message to the recipient's
     * {@code receiveMessage} method, simulating direct message passing.</p>
     *
     * @param sender the Player sending the message
     * @param recipient the Player receiving the message
     * @param message the Message content to be delivered
     * @throws IllegalArgumentException if any parameter is null
     */
    public void deliverMessage(Player sender, Player recipient, Message message) {
        validateDeliveryParameters(sender, recipient, message);

            try {
                recipient.receiveMessage(message);
            } catch (Exception e) {
                ExceptionHandler.handle(new PlayerNotFoundException(
                        Constants.FAILED_TO_DELIVER_MESSAGE_FROM + sender.id() + Constants.TO + recipient.id()
                ));
            }


    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException(Constants.PLAYER_CANNOT_BE_NULL);
        }
    }

    private void validatePlayerId(String playerId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException(Constants.PLAYER_ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    private void validateDeliveryParameters(Player sender, Player recipient, Message message) {
        if (sender == null) {
            throw new IllegalArgumentException(Constants.SENDER_PLAYER_CANNOT_BE_NULL);
        }
        if (recipient == null) {
            throw new IllegalArgumentException(Constants.RECIPIENT_PLAYER_CANNOT_BE_NULL);
        }
        if (message == null) {
            throw new IllegalArgumentException(Constants.MESSAGE_CANNOT_BE_NULL);
        }
    }
}