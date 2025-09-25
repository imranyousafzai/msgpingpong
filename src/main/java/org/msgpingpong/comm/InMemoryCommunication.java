package org.msgpingpong.comm;

import org.msgpingpong.core.Constants;
import org.msgpingpong.exception.CommunicationException;
import org.msgpingpong.exception.PlayerNotFoundException;
import org.msgpingpong.exception.ExceptionHandler;
import org.msgpingpong.model.Message;
import org.msgpingpong.model.Player;
import org.msgpingpong.util.Logger;

import static org.msgpingpong.core.Constants.HYPHIN_TO_HYPHIN;
import static org.msgpingpong.core.Constants.MESSAGE_DELIVERY;

/**
 * Implementation of {@link CommunicationStrategy} that facilitates in-memory message passing
 * between players within a single Java Virtual Machine (JVM) process.
 *
 * <p>This strategy utilizes a {@link Mediator} pattern to decouple communication between
 * {@link Player} instances, allowing them to interact without direct references to each other.
 * The mediator acts as a central hub for routing all messages within the application.</p>
 *
 * <p><b>Usage Context:</b> This implementation is designed for single-process environments
 * where all players operate within the same JVM. It provides efficient, low-latency communication
 * without network overhead.</p>
 *
 * <p><b>Key Responsibilities:</b></p>
 * <ul>
 *   <li>Delegates message delivery to the underlying {@link Mediator} instance</li>
 *   <li>Provides player lookup functionality through the mediator's registry</li>
 *   <li>Maintains loose coupling between sender and receiver players</li>
 * </ul>
 *
 * @see CommunicationStrategy
 * @see Mediator
 * @see Player
 * @see Message
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class InMemoryCommunication implements CommunicationStrategy {

    private final Mediator mediator;

    /**
     * Constructs a new InMemoryCommunication instance with the specified mediator.
     *
     * @param mediator the mediator instance that will handle message routing and player
     *                 management; must not be null
     * @throws IllegalArgumentException if the provided mediator is null
     */
    public InMemoryCommunication(Mediator mediator) {
        if (mediator == null) {
            throw new IllegalArgumentException(Constants.MEDIATOR_CANNOT_BE_NULL);
        }
        this.mediator = mediator;
    }

    /**
     * Sends a message from one player to another using the underlying mediator.
     *
     * @param from the player sending the message; must not be null
     * @param to the player receiving the message; must not be null
     * @param msg the message content to be delivered; must not be null
     * @throws IllegalArgumentException if any parameter is null
     */
    @Override
    public void sendMessage(Player from, Player to, Message msg) {

        validateMessageParameters(from, to, msg);
        // Use a separate thread for message delivery to avoid blocking
        Thread deliveryThread = new Thread(() -> {
            try {
                mediator.deliverMessage(from, to, msg);
                logMessageDelivery(from, to, msg);

            } catch (Exception e) {
                handleCommunicationException(Constants.FAILED_TO_SEND_MESSAGE, e);
            }
        }, MESSAGE_DELIVERY + from.id() + HYPHIN_TO_HYPHIN + to.id());

        deliveryThread.start();
    }

    /**
     * Retrieves a player by their unique identifier from the mediator's registry.
     *
     * @param playerId the unique identifier of the player to find; must not be null or empty
     * @return the found player, or null if not found and exception is handled
     * @throws IllegalArgumentException if playerId is null or empty
     */
    @Override
    public Player findPlayer(String playerId) {
        validatePlayerId(playerId);

        try {
            return mediator.find(playerId)
                    .orElseThrow(() -> new PlayerNotFoundException(Constants.PLAYER_NOT_FOUND + playerId));

        } catch (PlayerNotFoundException e) {
            ExceptionHandler.handle(e);
            return null;

        } catch (Exception e) {
            handleCommunicationException(Constants.UNEXPECTED_ERROR_FINDING_PLAYER + playerId, e);
            return null;
        }
    }

    /**
     * Validates that all message parameters are non-null.
     *
     * @param from the sender player
     * @param to the recipient player
     * @param msg the message content
     * @throws IllegalArgumentException if any parameter is null
     */
    private void validateMessageParameters(Player from, Player to, Message msg) {
        if (from == null) {
            throw new IllegalArgumentException(Constants.SENDER_PLAYER_CANNOT_BE_NULL);
        }
        if (to == null) {
            throw new IllegalArgumentException(Constants.RECEIVER_PLAYER_CANNOT_BE_NULL);
        }
        if (msg == null) {
            throw new IllegalArgumentException(Constants.MESSAGE_CANNOT_BE_NULL);
        }
    }

    /**
     * Validates that the player ID is not null or empty.
     *
     * @param playerId the player identifier to validate
     * @throws IllegalArgumentException if playerId is null or empty
     */
    private void validatePlayerId(String playerId) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty");
        }
    }

    /**
     * Logs successful message delivery.
     *
     * @param from the sender player
     * @param to the recipient player
     * @param msg the delivered message
     */
    private void logMessageDelivery(Player from, Player to, Message msg) {
        Logger.info(Constants.COMPONENT_NAME,
                String.format(Constants.MESSAGE_DELIVERED_FROM_TO_CONTENT,
                        from.id(), to.id(), msg.content()));
    }

    /**
     * Handles communication exceptions using the centralized exception handler.
     *
     * @param message the error message
     * @param cause the original exception
     */
    private void handleCommunicationException(String message, Exception cause) {
        ExceptionHandler.handle(new CommunicationException(message, cause));
    }
}