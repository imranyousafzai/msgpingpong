package org.msgpingpong.model;

/**
 * Represents a message exchanged between two Players.
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Encapsulate the textual content of the message.</li>
 *   <li>Track the sender and receiver IDs for routing and logging.</li>
 *   <li>Remain immutable once created, ensuring message integrity.</li>
 * </ul>
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public record Message(String content, String senderId, String receiverId) {}

