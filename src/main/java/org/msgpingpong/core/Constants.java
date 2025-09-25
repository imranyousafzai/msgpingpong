package org.msgpingpong.core;

import java.time.format.DateTimeFormatter;

/**
 * Centralized constants for the messaging system.
 * Provides a single source of truth for all configuration values and magic numbers.
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class Constants {

    public static final String COMPONENT_NAME = "InMemoryCommunication";
    public static final String MEDIATOR_CANNOT_BE_NULL = "Mediator cannot be null";
    public static final String FAILED_TO_SEND_MESSAGE = "Failed to send message";
    public static final String PLAYER_NOT_FOUND = "Player not found: ";
    public static final String UNEXPECTED_ERROR_FINDING_PLAYER = "Unexpected error finding player: ";
    public static final String SENDER_PLAYER_CANNOT_BE_NULL = "Sender player cannot be null";
    public static final String RECEIVER_PLAYER_CANNOT_BE_NULL = "Receiver player cannot be null";
    public static final String MESSAGE_CANNOT_BE_NULL = "Message cannot be null";
    public static final String MESSAGE_DELIVERED_FROM_TO_CONTENT = "Message delivered from %s to %s - content: %s";
    public static final String PLAYER_CANNOT_BE_NULL = "Player cannot be null";
    public static final String PLAYER_ID_CANNOT_BE_NULL_OR_EMPTY = "Player ID cannot be null or empty";
    public static final String RECIPIENT_PLAYER_CANNOT_BE_NULL = "Recipient player cannot be null";
    public static final String FAILED_TO_DELIVER_MESSAGE_FROM = "Failed to deliver message from '";
    public static final String TO = "' to '";
    public static final String MESSAGE_DELIMITER = "|";
    public static final String NULL_PARAMETER_MESSAGE = "Sender, receiver, and message must not be null";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error sending message";
    public static final String LOOKUP_ERROR_MESSAGE = "Player lookup operation not supported in socket communication mode";
    public static final String HOST_MUST_NOT_BE_NULL_OR_EMPTY = "Host must not be null or empty";
    public static final String PORT_MUST_BE_BETWEEN_0_AND_65535 = "Port must be between 0 and 65535";
    public static final String PLAYER_LOOKUP_NOT_SUPPORTED_IN_SOCKET_COMMUNICATION_MODE = "Player lookup not supported in socket communication mode";
    public static final String GAME_CONTROLLER = "GameController";
    public static final int MAX_MESSAGE_EXCHANGES = 2;
    public static final String INITIATOR_ID = "P1";
    public static final String RESPONDER_ID = "P2";
    public static final String INITIAL_MESSAGE = "Hello";
    public static final String UNEXPECTED_RUNTIME_ERROR = "Unexpected runtime error";
    public static final String GAME_FINISHED_AFTER = "Game finished after ";
    public static final String EXCHANGES = " exchanges.";
    public static final String ONE_OR_BOTH_PLAYERS_ARE_NULL = "One or both players are null";
    public static final String FAILED_TO_SEND_MESSAGE_IN_ROUND = "Failed to send message in round ";
    public static final String ROUND = "Round ";
    public static final String COMPLETE_LAST_MESSAGE = " complete. Last message: ";
    public static final String HOST = "localhost";
    public static final int PORT = 5000;
    public static final String MESSAGE_PREFIX = "Message-";
    public static final String FIELD_SEPARATOR = "|";
    public static final String PLAYER_CLIENT = "PlayerClient";
    public static final String CLIENT_STARTING_CONNECTING_TO_SERVER = "Client starting... connecting to server...";
    public static final String COMPLETED = "Completed ";
    public static final String UNEXPECTED_CLIENT_ERROR = "Unexpected client error";
    public static final String CLIENT_SHUTTING_DOWN_GRACEFULLY = "Client shutting down gracefully.";
    public static final String ERROR_COMMUNICATING_WITH_SERVER_DURING_EXCHANGE = "Error communicating with server during exchange ";
    public static final String EXCHANGE_D_SENT_S_RECEIVED_S = "Exchange %d: Sent '%s', Received '%s'";
    public static final String SENT = "Sent: ";
    public static final String MESSAGE_EXCHANGES_STOPPING = " message exchanges. Stopping...";
    public static final String SERVER_NAME = "PlayerServer";
    public static final String FATAL_SERVER_ERROR = "Fatal server error";
    public static final String SERVER_STARTING_ON_PORT = "Server starting on port ";
    public static final String STRING = "...";
    public static final String LISTENING_FOR_CLIENT_CONNECTIONS_ON_PORT = "Listening for client connections on port ";
    public static final String FAILED_TO_START_SERVER_ON_PORT = "Failed to start server on port ";
    public static final String SERVER_SHUTTING_DOWN_GRACEFULLY = "Server shutting down gracefully.";
    public static final String REACHED_MAXIMUM_OF = "Reached maximum of ";
    public static final String MESSAGES_STOPPING = " messages. Stopping...";
    public static final String ERROR_HANDLING_CLIENT_CONNECTION = "Error handling client connection";
    public static final String REGEX = "\\|";
    public static final String EXCHANGE_RECEIVED_REPLYING = "Exchange #%d: Received '%s' -> Replying '%s'";
    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final String CAUSED_BY = "Caused by: ";
    public static final String S_ERROR_S_N = "[%s] ERROR: %s%n";
    public static final String INVALID_MODE = "Invalid mode: ";
    public static final String USE_SINGLE_SERVER_OR_CLIENT = ". Use single, server, or client.";
    public static final String COLON_STRING = ": ";
    public static final int THREAD_POOL_SIZE = 4;
    public static final String SIMULATE_CONCURRENT_MESSAGE_EXCHANGE = "simulateConcurrentMessageExchange";
    public static final String ERROR_IN_ROUND = "Error in round ";
    public static final String MESSAGE_DELIVERY = "MessageDelivery-";
    public static final String HYPHIN_TO_HYPHIN = "-to-";

    private Constants() {
        // Utility class - prevent instantiation
    }

}
