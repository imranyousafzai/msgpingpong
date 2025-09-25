package org.msgpingpong.exception;

import org.msgpingpong.core.Constants;

import java.time.LocalDateTime;

/**
 * Centralized exception handler for logging and optional recovery.
 * Helps keep exception management consistent across the project.
 */
public final class ExceptionHandler {

    private ExceptionHandler() {}

    /**
     * Handle an exception in a professional, centralized way.
     * Logs the error with timestamp and decides recovery action.
     *
     * @param e the exception to handle
     */
    public static void handle(Exception e) {
        String timestamp = LocalDateTime.now().format(Constants.FORMATTER);
        System.err.printf(Constants.S_ERROR_S_N, timestamp, e.getMessage());
        if (e.getCause() != null) {
            System.err.println(Constants.CAUSED_BY + e.getCause());
        }
        e.printStackTrace(System.err);
    }
}
