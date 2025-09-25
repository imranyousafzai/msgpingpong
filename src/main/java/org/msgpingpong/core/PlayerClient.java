package org.msgpingpong.core;

import org.msgpingpong.util.Logger;
import org.msgpingpong.exception.CommunicationException;
import org.msgpingpong.exception.ExceptionHandler;

import java.io.*;
import java.net.*;

/**
 * PlayerClient is a standalone process representing the initiating Player in multi-process mode.
 * Establishes TCP connection with PlayerServer to conduct message ping-pong exchanges.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Connect to the PlayerServer via TCP on configured host and port</li>
 *   <li>Send formatted messages and await responses</li>
 *   <li>Log all message exchanges for monitoring and debugging</li>
 *   <li>Execute exactly 10 round-trip message exchanges</li>
 *   <li>Shut down gracefully after completing all exchanges</li>
 * </ul>
 *
 * <p><b>Protocol Details:</b></p>
 * Messages are formatted as: {@code content|sequenceNumber}
 *
 * <p>This class demonstrates the "multi-process" requirement where each Player
 * runs in a separate Java process (different PID).</p>
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class PlayerClient {

    /**
     * Main entry point for the PlayerClient application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Logger.info(Constants.PLAYER_CLIENT, Constants.CLIENT_STARTING_CONNECTING_TO_SERVER);

        try {
            executeMessageExchanges();
            Logger.info(Constants.PLAYER_CLIENT, Constants.COMPLETED + Constants.MAX_MESSAGE_EXCHANGES + Constants.MESSAGE_EXCHANGES_STOPPING);
        } catch (CommunicationException e) {
            ExceptionHandler.handle(e);
        } catch (Exception e) {
            ExceptionHandler.handle(new CommunicationException(Constants.UNEXPECTED_CLIENT_ERROR, e));
        } finally {
            Logger.info(Constants.PLAYER_CLIENT, Constants.CLIENT_SHUTTING_DOWN_GRACEFULLY);
        }
    }

    /**
     * Executes the series of message exchanges with the server.
     *
     * @throws CommunicationException if communication fails during the exchanges
     */
    private static void executeMessageExchanges() throws CommunicationException {
        for (int counter = 0; counter < Constants.MAX_MESSAGE_EXCHANGES; counter++) {
            executeSingleExchange(counter);
        }
    }

    /**
     * Executes a single message exchange with the server.
     *
     * @param sequenceNumber the current sequence number for this exchange
     * @throws CommunicationException if the exchange fails
     */
    private static void executeSingleExchange(int sequenceNumber) throws CommunicationException {
        try (Socket socket = createSocketConnection();
             PrintWriter writer = createWriter(socket);
             BufferedReader reader = createReader(socket)) {

            String message = formatMessage(sequenceNumber);
            sendMessage(writer, message);
            String reply = receiveReply(reader);
            logExchange(sequenceNumber, message, reply);

        } catch (IOException e) {
            throw new CommunicationException(Constants.ERROR_COMMUNICATING_WITH_SERVER_DURING_EXCHANGE +
                    (sequenceNumber + 1), e);
        }
    }

    /**
     * Creates a socket connection to the server.
     *
     * @return the established socket connection
     * @throws IOException if connection fails
     */
    private static Socket createSocketConnection() throws IOException {
        return new Socket(Constants.HOST, Constants.PORT);
    }

    /**
     * Creates a PrintWriter for the socket output stream.
     *
     * @param socket the connected socket
     * @return PrintWriter for sending messages
     * @throws IOException if stream creation fails
     */
    private static PrintWriter createWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Creates a BufferedReader for the socket input stream.
     *
     * @param socket the connected socket
     * @return BufferedReader for receiving messages
     * @throws IOException if stream creation fails
     */
    private static BufferedReader createReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Formats the message with sequence number.
     *
     * @param sequenceNumber the current sequence number
     * @return formatted message string
     */
    private static String formatMessage(int sequenceNumber) {
        return Constants.MESSAGE_PREFIX + (sequenceNumber + 1) + Constants.FIELD_SEPARATOR + sequenceNumber;
    }

    /**
     * Sends a message to the server.
     *
     * @param writer the PrintWriter to use for sending
     * @param message the message to send
     */
    private static void sendMessage(PrintWriter writer, String message) {
        writer.println(message);
        Logger.info(Constants.PLAYER_CLIENT, Constants.SENT + extractContent(message));
    }

    /**
     * Receives and processes a reply from the server.
     *
     * @param reader the BufferedReader to use for receiving
     * @return the received reply, or null if no reply received
     * @throws IOException if reading fails
     */
    private static String receiveReply(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    /**
     * Logs the message exchange details.
     *
     * @param sequenceNumber the exchange sequence number
     * @param sentMessage the message that was sent
     * @param receivedReply the reply that was received
     */
    private static void logExchange(int sequenceNumber, String sentMessage, String receivedReply) {
        if (receivedReply != null) {
            Logger.info(Constants.PLAYER_CLIENT,
                    String.format(Constants.EXCHANGE_D_SENT_S_RECEIVED_S,
                            sequenceNumber + 1,
                            extractContent(sentMessage),
                            receivedReply));
        }
    }

    /**
     * Extracts the content portion from a formatted message.
     *
     * @param formattedMessage the full formatted message
     * @return the content portion before the separator
     */
    private static String extractContent(String formattedMessage) {
        int separatorIndex = formattedMessage.indexOf(Constants.FIELD_SEPARATOR);
        return separatorIndex > 0 ? formattedMessage.substring(0, separatorIndex) : formattedMessage;
    }
}