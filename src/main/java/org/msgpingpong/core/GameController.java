package org.msgpingpong.core;

import org.msgpingpong.comm.CommunicationStrategy;
import org.msgpingpong.comm.InMemoryCommunication;
import org.msgpingpong.comm.Mediator;
import org.msgpingpong.exception.CommunicationException;
import org.msgpingpong.exception.ExceptionHandler;
import org.msgpingpong.exception.GameException;
import org.msgpingpong.exception.PlayerNotFoundException;
import org.msgpingpong.model.Message;
import org.msgpingpong.model.Player;
import org.msgpingpong.util.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Multithreaded GameController orchestrates the messaging simulation in single-process mode.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Create two Player instances: an initiator and a responder.</li>
 *   <li>Configure the CommunicationStrategy (e.g., InMemoryCommunication).</li>
 *   <li>Run the loop where the initiator sends 10 messages and receives 10 replies.</li>
 *   <li>Stop gracefully when the conversation reaches the required limit.</li>
 * </ul>
 *
 * <p>For multi-process mode, this role is replaced by the {@link PlayerClient}
 * and {@link PlayerServer} classes.</p>
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class GameController {
    private static final ExecutorService executor = Executors.newFixedThreadPool(Constants.THREAD_POOL_SIZE);

    private GameController() {
        // Prevent instantiation
    }

    /**
     * Main entry point for the single-process messaging game.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Instant start = Instant.now();  // Start time

        try {
            runGame();
        } catch (GameException ge) {
            ExceptionHandler.handle(ge);
        } catch (Exception e) {
            ExceptionHandler.handle(new CommunicationException(Constants.UNEXPECTED_RUNTIME_ERROR, e));
        } finally {
            shutdownExecutor();
        }

        try {
            if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
                Instant end = Instant.now();
                Duration totalTime = Duration.between(start, end);
                Logger.info(Constants.GAME_CONTROLLER, Constants.GAME_FINISHED_AFTER + Constants.MAX_MESSAGE_EXCHANGES + Constants.EXCHANGES);
                Logger.info("GameController", "Total time consumed for "
                        + Constants.MAX_MESSAGE_EXCHANGES + " rounds: " + totalTime.toMillis() + " ms");
            } else {
                Logger.error("GameController", "Executor did not finish in expected time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.error("GameController", "Execution interrupted: " + e.getMessage());
        }
    }

    /**
     * Executes the complete messaging game simulation.
     */
    private static void runGame() {
        Logger.info(Constants.GAME_CONTROLLER, "Starting multithreaded single-process messaging game...");

        Mediator mediator = new Mediator();
        CommunicationStrategy strategy = new InMemoryCommunication(mediator);

        Player player1 = createAndRegisterPlayer(Constants.INITIATOR_ID, strategy, mediator);
        Player player2 = createAndRegisterPlayer(Constants.RESPONDER_ID, strategy, mediator);

        simulateConcurrentMessageExchange(player1, player2, strategy);
    }

    private static void simulateConcurrentMessageExchange(Player initiator, Player responder, CommunicationStrategy strategy) {
        AtomicReference<String> currentMessage = new AtomicReference<>(Constants.INITIAL_MESSAGE);

        for (int round = 1; round <= Constants.MAX_MESSAGE_EXCHANGES; round++) {
            int currentRound = round;

            executor.submit(() -> {
                try {
                    validatePlayers(initiator, responder);
                    String messageContent = currentMessage.get();
                    sendMessage(initiator, responder, messageContent, strategy, currentRound);
                    String updatedMessage = updateMessageContent(messageContent, currentRound);
                    currentMessage.set(updatedMessage);
                    logRoundCompletion(currentRound, updatedMessage);
                } catch (Exception e) {
                    Logger.error(Constants.SIMULATE_CONCURRENT_MESSAGE_EXCHANGE,
                            Constants.ERROR_IN_ROUND + currentRound + Constants.COLON_STRING + e.getMessage());
                }
            });
        }
    }

    private static void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Creates and registers a player with the mediator.
     *
     * @param id        the player's unique identifier
     * @param strategy  the communication strategy
     * @param mediator  the mediator for player registration
     * @return the created Player instance
     */
    private static Player createAndRegisterPlayer(String id, CommunicationStrategy strategy, Mediator mediator) {
        Player player = new Player(id, strategy);
        mediator.register(player);
        return player;
    }

    /**
     * Validates that both players are not null.
     *
     * @param player1 the first player to validate
     * @param player2 the second player to validate
     * @throws PlayerNotFoundException if either player is null
     */
    private static void validatePlayers(Player player1, Player player2) {
        if (player1 == null || player2 == null) {
            throw new PlayerNotFoundException(Constants.ONE_OR_BOTH_PLAYERS_ARE_NULL);
        }
    }

    /**
     * Sends a message from one player to another.
     *
     * @param from     the sender player
     * @param to       the receiver player
     * @param content  the message content
     * @param strategy the communication strategy
     * @param round    the current round number
     */
    private static void sendMessage(Player from, Player to, String content,
                                    CommunicationStrategy strategy, int round) {
        try {
            Message message = new Message(content, from.id(), to.id());
            strategy.sendMessage(from, to, message);
        } catch (Exception e) {
            throw new CommunicationException(Constants.FAILED_TO_SEND_MESSAGE_IN_ROUND + round, e);
        }
    }

    /**
     * Updates the message content for the next round.
     *
     * @param currentMessage the current message content
     * @param round          the current round number
     * @return the updated message content
     */
    private static String updateMessageContent(String currentMessage, int round) {
        return currentMessage + "-" + round;
    }

    /**
     * Logs the completion of a message exchange round.
     *
     * @param round          the completed round number
     * @param currentMessage the message content after the round
     */
    private static void logRoundCompletion(int round, String currentMessage) {
        Logger.info(Constants.GAME_CONTROLLER,
                Constants.ROUND + round + Constants.COMPLETE_LAST_MESSAGE + currentMessage);
    }
}