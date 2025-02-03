package org.ipr;


import java.nio.charset.Charset;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start");
        var logFileDivider =
                new LogFileDivider("src/main/resources/main.log", "Windows-1251", 10);
        logFileDivider.clearAllFilesFromDirPath();
        logFileDivider.divide();
        System.out.println("OK");

        System.out.println("Start 2");
        var logParser = new LogRegexParser("src/main/resources/my-logs", "src/main/resources/my-logs/filter-output");
        logParser.clearAllFilesFromFilterOutputDirPath();
        logParser.parseLogFromFiles("my-log-*.log", "Windows-1251", "INFO");
        logParser.parseLogFromFiles("my-log-*.log", "Windows-1251", "WARN");
        logParser.parseLogFromFiles("my-log-*.log", "Windows-1251", "DEBUG");
        logParser.parseLogFromFiles("my-log-*.log", "Windows-1251", "TRACE");
        System.out.println("OK");

        System.out.println("Start 3");
        int c1 = LogSeparatorInserter.insertAndMakeCSV(
                "|",
                "src/main/resources/my-logs/filter-output/my-log-[AnyChars]@INFO.log",
                "Windows-1251"
        );
        int c2 = LogSeparatorInserter.insertAndMakeCSV(
                "|",
                "src/main/resources/my-logs/filter-output/my-log-[AnyChars]@WARN.log",
                "Windows-1251"
        );
        int c3 = LogSeparatorInserter.insertAndMakeCSV(
                "|",
                "src/main/resources/my-logs/filter-output/my-log-[AnyChars]@DEBUG.log",
                "Windows-1251"
        );
        int c4 = LogSeparatorInserter.insertAndMakeCSV(
                "|",
                "src/main/resources/my-logs/filter-output/my-log-[AnyChars]@TRACE.log",
                "Windows-1251"
        );
        System.out.println("OK");
        System.out.println(c1 + " " + c2 + " " + c3 + " " + c4);

    }
}