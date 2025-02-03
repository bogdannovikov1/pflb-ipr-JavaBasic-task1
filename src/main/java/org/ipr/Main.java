package org.ipr;


public class Main {
    public static void main(String[] args) {
        System.out.println("Start");
        var logFileDivider =
                new LogFileDivider("src/main/resources/main.log", "Windows-1251", 20);
        logFileDivider.clearAllFilesFromDirPath();
        logFileDivider.divide();
        System.out.println("OK");


        System.out.println("Start 2");
        var logParser = new LogParser(logFileDivider.getDirPath());
        logParser.clearAllFilesFromFilterOutputDirPath();
        String regexFilename1 = logParser.parseLogFromFile("my-log-1.log", "Windows-1251", "price");
        String regexFilename2 = logParser.parseLogFromFiles("my-*-??.log", "Windows-1251", "price");
        System.out.println(regexFilename1);
        System.out.println(regexFilename2);
        System.out.println("OK 2");

    }
}