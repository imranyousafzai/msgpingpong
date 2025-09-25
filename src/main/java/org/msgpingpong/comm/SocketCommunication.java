package org.msgpingpong.comm;

import org.msgpingpong.core.Constants;
import org.msgpingpong.exception.CommunicationException;
import org.msgpingpong.exception.ExceptionHandler;
import org.msgpingpong.model.Message;
import org.msgpingpong.model.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Socket-based communication strategy for inter-process messaging in a distributed
 * player system. This implementation enables communication between Player instances
 * running in separate JVM processes through TCP socket connections.
 *
 * <p><b>Key Characteristics:</b></p>
 * <ul>
 *   <li>Uses TCP sockets for reliable, connection-oriented communication</li>
 *   <li>Serializes messages into pipe-delimited format for network transmission</li>
 *   <li>Designed for multi-process deployment where each Player runs in its own JVM</li>
 *   <li>Implements the CommunicationStrategy interface for polymorphic behavior</li>
 * </ul>
 *
 * <p><b>Protocol Format:</b></p>
 * Messages are serialized as: {@code content|senderId|receiverId}
 *
 * <p><b>Usage Note:</b></p>
 * This strategy requires a corresponding socket server (typically {@code PlayerServer})
 * to be running and listening on the specified host and port to receive and route messages.
 *
 * <p><b>Limitations:</b></p>
 * <ul>
 *   <li>Does not support player discovery/lookup operations</li>
 *   <li>Requires network connectivity between processes</li>
 *   <li>Message delivery is synchronous and may block on network I/O</li>
 * </ul>
 *
 * @see CommunicationStrategy
 * @see InMemoryCommunication
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class SocketCommunication implements CommunicationStrategy {

    private final String host;
    private final int port;
    private final BlockingQueue<Socket> connectionPool;
    private final int poolSize = 10;

    /**
     * Constructs a new SocketCommunication instance targeting the specified
     * host and port for message delivery.
     *
     * @param host the hostname or IP address of the target socket server
     * @param port the port number on which the target server is listening
     * @throws IllegalArgumentException if the port is out of valid range (0-65535) or host is null/empty
     */
    public SocketCommunication(String host, int port) {
        validateConstructorParameters(host, port);
        this.host = host;
        this.port = port;
        this.connectionPool = new LinkedBlockingQueue<>(poolSize);
        initializeConnectionPool();
    }

    private void initializeConnectionPool() {
        for (int i = 0; i < poolSize; i++) {
            try {
                connectionPool.put(new Socket(host, port));
            } catch (Exception e) {
                ExceptionHandler.handle(new CommunicationException("Failed to initialize connection pool", e));
            }
        }
    }

    private Socket getConnection() throws InterruptedException, IOException {
        Socket socket = connectionPool.poll(5, TimeUnit.SECONDS);
        if (socket == null || socket.isClosed()) {
            socket = new Socket(host, port);
        }
        return socket;
    }

    private void returnConnection(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                connectionPool.put(socket);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                try {
                    socket.close();
                } catch (IOException ioException) {
                    // Ignore
                }
            }
        }
    }

    private void validateConstructorParameters(String host, int port) {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException(Constants.HOST_MUST_NOT_BE_NULL_OR_EMPTY);
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException(Constants.PORT_MUST_BE_BETWEEN_0_AND_65535);
        }
    }

    @Override
    public void sendMessage(Player from, Player to, Message msg) {
        Socket socket = null;
        try {
            validateMessageParameters(from, to, msg);
            socket = getConnection();
            sendMessageViaSocket(socket, msg);
        } catch (Exception e) {
            ExceptionHandler.handle(new CommunicationException(Constants.UNEXPECTED_ERROR_MESSAGE, e));
        } finally {
            returnConnection(socket);
        }
    }

    private void validateMessageParameters(Player from, Player to, Message msg) {
        if (from == null || to == null || msg == null) {
            throw new CommunicationException(Constants.NULL_PARAMETER_MESSAGE);
        }
    }

    private void sendMessageViaSocket(Socket socket, Message msg) throws IOException {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String serializedMessage = serializeMessage(msg);
            out.println(serializedMessage);
        }
    }

    private String serializeMessage(Message msg) {
        return String.join(Constants.MESSAGE_DELIMITER,
                msg.content(),
                msg.senderId(),
                msg.receiverId()
        );
    }

    /**
     * Player lookup is not supported in socket communication mode as players
     * are typically managed by a remote server process rather than locally.
     *
     * @param playerId the ID of the player to locate
     * @throws UnsupportedOperationException always thrown, as this operation
     *         is not supported in socket communication mode
     * @return nothing (always throws exception)
     */
    @Override
    public Player findPlayer(String playerId) {
        ExceptionHandler.handle(new CommunicationException(Constants.LOOKUP_ERROR_MESSAGE));
        throw new UnsupportedOperationException(Constants.PLAYER_LOOKUP_NOT_SUPPORTED_IN_SOCKET_COMMUNICATION_MODE);
    }
}