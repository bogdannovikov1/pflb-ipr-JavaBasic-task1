package org.ipr;

import java.nio.file.Path;

public class Test1 {
    public static void main(String[] args) {
        LogParser logParser = new LogParser("src/main/resources/", "Windows-1251");

        logParser.setHelp(true);

        logParser.clearDivideDir();
        logParser.clearFilterDir();

        String mainDividedMask = logParser.divide("main.log", 10);
        Path traceFilterFile = logParser.foundRegex(mainDividedMask, "TRACE|WARN|INFO");

        int ignored = logParser.insertSepatator("|", traceFilterFile);
    }
}
