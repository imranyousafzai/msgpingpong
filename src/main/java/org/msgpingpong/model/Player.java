package org.msgpingpong.model;

import org.msgpingpong.comm.CommunicationStrategy;
import org.msgpingpong.core.Constants;
import org.msgpingpong.util.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a Player in the messaging system that can participate in message
 * ping-pong exchanges using various communication strategies.
 *
 * <p><b>Core Responsibilities:</b></p>
 * <ul>
 *   <li>Maintain a unique identifier for message routing and player recognition</li>
 *   <li>Send messages to other players using the configured communication strategy</li>
 *   <li>Receive incoming messages, process them, and generate appropriate replies</li>
 *   <li>Track and manage message counters for both sent and received messages</li>
 *   <li>Support multiple communication modes (in-memory mediator vs socket-based)</li>
 * </ul>
 *
 * <p><b>Message Handling Behavior:</b></p>
 * <ul>
 *   <li>Automatically appends a sequence counter to received message content</li>
 *   <li>Limits message exchanges to a maximum of 10 ping-pong cycles</li>
 *   <li>Uses the original sender reference for replies to prevent infinite loops</li>
 *   <li>Provides real-time console feedback for message reception events</li>
 * </ul>
 *
 * @see CommunicationStrategy
 * @see Message
 * @see Logger
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class Player {
    private final String id;
    private final AtomicInteger receiveCount = new AtomicInteger(0);
    private final CommunicationStrategy strategy;

    /**
     * Constructs a new Player with the specified identifier and communication strategy.
     *
     * @param id the unique identifier for this player (cannot be null or empty)
     * @param strategy the communication strategy implementation for message delivery
     * @throws IllegalArgumentException if id is null or empty, or strategy is null
     */
    public Player(String id, CommunicationStrategy strategy) {
        this.id = id;
        this.strategy = strategy;
    }

    public String id() {
        return id;
    }

    public void receiveMessage(Message message) {
        int currentReceiveCount = receiveCount.incrementAndGet();
        Logger.info(id + " received: " + message.content() +" (total received: " + currentReceiveCount + ")");

        if (currentReceiveCount <= Constants.MAX_MESSAGE_EXCHANGES && !message.content().endsWith("-"+Constants.MAX_MESSAGE_EXCHANGES)) {
            String replyContent = message.content() + "-" + currentReceiveCount;
            Player originalSender = strategy.findPlayer(message.senderId());
            if (originalSender != null) {
                sendMessage(replyContent, originalSender);
            }
        }
    }

    public void sendMessage(String content, Player receiver) {
        Message msg = new Message(content, this.id(), receiver.id());
        strategy.sendMessage(this, receiver, msg);
    }
}