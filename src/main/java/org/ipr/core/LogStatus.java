package org.ipr.core;

public enum LogStatus {
    TRACE, DEBUG, INFO, WARN, ERROR, FATAL;

    public static LogStatus fromString(String level) {
        try {
            return LogStatus.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid log level: " + level);
        }
    }
}
