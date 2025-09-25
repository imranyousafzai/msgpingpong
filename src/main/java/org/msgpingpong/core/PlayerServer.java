package org.msgpingpong.core;

import org.msgpingpong.util.Logger;
import org.msgpingpong.exception.CommunicationException;
import org.msgpingpong.exception.ExceptionHandler;

import java.io.*;
import java.net.*;

/**
 * PlayerServer is a standalone process representing one Player in multi-process mode.
 * Listens for TCP connections, processes messages, and maintains conversation state.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Listen for incoming TCP connections from client Players</li>
 *   <li>Process received messages by appending a sequence counter</li>
 *   <li>Send replies back to the client</li>
 *   <li>Track message processing count and stop gracefully after maximum exchanges</li>
 *   <li>Handle network I/O errors and connection issues robustly</li>
 * </ul>
 *
 * <p><b>Protocol:</b> Messages are expected in format: {@code content|senderId|receiverId}</p>
 *
 * <p>This class demonstrates the "multi-process" requirement where each Player
 * runs in a separate Java process (different PID).</p>
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class PlayerServer {

    /**
     * Main entry point for the Player server process.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            startServer();
        } catch (Exception e) {
            ExceptionHandler.handle(new CommunicationException(Constants.FATAL_SERVER_ERROR, e));
            System.exit(1);
        }
    }

    /**
     * Starts the server and begins listening for client connections.
     */
    private static void startServer() {
        Logger.info(Constants.SERVER_NAME, Constants.SERVER_STARTING_ON_PORT + Constants.PORT + Constants.STRING);

        try (ServerSocket serverSocket = new ServerSocket(Constants.PORT)) {
            Logger.info(Constants.SERVER_NAME, Constants.LISTENING_FOR_CLIENT_CONNECTIONS_ON_PORT + Constants.PORT);
            processIncomingConnections(serverSocket);

        } catch (IOException e) {
            ExceptionHandler.handle(new CommunicationException(Constants.FAILED_TO_START_SERVER_ON_PORT + Constants.PORT, e));
        } finally {
            Logger.info(Constants.SERVER_NAME, Constants.SERVER_SHUTTING_DOWN_GRACEFULLY);
        }
    }

    /**
     * Processes incoming client connections until message limit is reached.
     *
     * @param serverSocket the server socket accepting connections
     */
    private static void processIncomingConnections(ServerSocket serverSocket) {
        int messageCount = 0;

        while (messageCount < Constants.MAX_MESSAGE_EXCHANGES) {
            try {
                messageCount = handleClientConnection(serverSocket, messageCount);
            } catch (CommunicationException e) {
                ExceptionHandler.handle(e);
                // Continue processing other connections despite individual client errors
            }
        }

        Logger.info(Constants.SERVER_NAME, Constants.REACHED_MAXIMUM_OF + Constants.MAX_MESSAGE_EXCHANGES + Constants.MESSAGES_STOPPING);
    }

    /**
     * Handles a single client connection and processes one message exchange.
     *
     * @param serverSocket the server socket to accept connections from
     * @param currentCount the current message count before processing
     * @return the updated message count after processing
     * @throws CommunicationException if connection or message processing fails
     */
    private static int handleClientConnection(ServerSocket serverSocket, int currentCount)
            throws CommunicationException {

        try (Socket clientSocket = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String receivedMessage = readClientMessage(reader);
            if (receivedMessage != null) {
                String reply = processMessage(receivedMessage, currentCount + 1);
                sendReply(writer, reply);
                logExchange(receivedMessage, reply, currentCount + 1);
                return currentCount + 1;
            }

        } catch (IOException e) {
            throw new CommunicationException(Constants.ERROR_HANDLING_CLIENT_CONNECTION, e);
        }

        return currentCount;
    }

    /**
     * Reads a message from the client input stream.
     *
     * @param reader the BufferedReader for client input
     * @return the received message content, or null if no message
     * @throws IOException if reading from stream fails
     */
    private static String readClientMessage(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    /**
     * Processes the received message and generates a reply.
     *
     * @param message the raw message received from client
     * @param sequenceNumber the current sequence number for this exchange
     * @return the formatted reply message
     */
    private static String processMessage(String message, int sequenceNumber) {
        // Extract just the content part (ignore sender/receiver IDs for reply formatting)
        String content = extractMessageContent(message);
        return content + "-" + sequenceNumber;
    }

    /**
     * Extracts the content portion from the raw message.
     *
     * @param rawMessage the raw pipe-delimited message
     * @return the message content
     */
    private static String extractMessageContent(String rawMessage) {
        String[] parts = rawMessage.split(Constants.REGEX);
        return parts.length > 0 ? parts[0] : rawMessage;
    }

    /**
     * Sends a reply back to the client.
     *
     * @param writer the PrintWriter for client output
     * @param reply the reply message to send
     */
    private static void sendReply(PrintWriter writer, String reply) {
        writer.println(reply);
    }

    /**
     * Logs the message exchange details.
     *
     * @param receivedMessage the message received from client
     * @param reply the reply sent to client
     * @param sequenceNumber the sequence number of this exchange
     */
    private static void logExchange(String receivedMessage, String reply, int sequenceNumber) {
        String content = extractMessageContent(receivedMessage);
        Logger.info(Constants.SERVER_NAME,
                String.format(Constants.EXCHANGE_RECEIVED_REPLYING,
                        sequenceNumber, content, reply));
    }
}