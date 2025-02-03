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
        var logParser = new LogParser("src/main/resources/my-logs");
        logParser.clearAllFilesFromFilterOutputDirPath();
        logParser.parseLogFromFile("my-log-7.log", "Windows-1251", "price");
        System.out.println("OK 2");

    }
}