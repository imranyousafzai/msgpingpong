package org.msgpingpong.util;

import org.msgpingpong.core.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A lightweight logging utility providing timestamped console output for the messaging system.
 *
 * <p><b>Limitations:</b></p>
 * <ul>
 *   <li>Output is directed only to standard output/error streams</li>
 *   <li>No support for log file rotation or persistence</li>
 *   <li>Fixed log levels (INFO/ERROR only)</li>
 *   <li>No configuration options for log level filtering</li>
 * </ul>
 *
 * @see LocalDateTime
 * @see DateTimeFormatter
 * @see System#out
 * @see System#err
 *
 * @author imran
 * @version 1.0
 * @since 2025
 */
public final class Logger {

    private static final Object lock = new Object();

    public static void info(String message) {
        synchronized (lock) {
            System.out.printf("[%s] [INFO] %s%n",
                    LocalDateTime.now().format(Constants.FORMATTER),
                    message);
        }
    }

    public static void info(String source, String message) {
        synchronized (lock) {
            System.out.printf("[%s] [INFO] [%s] %s%n",
                    LocalDateTime.now().format(Constants.FORMATTER),
                    source,
                    message);
        }
    }

    public static void error(String source, String message) {
        synchronized (lock) {
            System.out.printf("[%s] [ERROR] [%s] %s%n",
                    LocalDateTime.now().format(Constants.FORMATTER),
                    source,
                    message);
        }
    }
}