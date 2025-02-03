package org.ipr;

public class FinalLogRegex {
    public static final String TRACE_REGEX = "^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+(TRACE:)\\s+([\\d\\.]+):([\\d]+)\\s+(.*)$";

    public static final String WARN_REGEX = "^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+(WARN)\\s*:\\s+([\\d\\.]+:\\d+\\.\\S+)\\s+(.*)$";

    public static final String DEBAG_REGEX = "^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+(DEBUG:)\\s+([\\w\\.\\d\\:]+)\\s+(.*)$";

    public static final String INFO_REGEX = "^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+(INFO)\\s*:\\s*(?:(\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d+)?(?:\\.[\\w]+)?)\\s+)?(.*)$";

    public static final String FOUND_LOG_STATUS_REGEX = "\\b(TRACE|WARN|DEBUG|INFO|ERROR|FATAL)\\b(?=\\s*:|:)";
}
