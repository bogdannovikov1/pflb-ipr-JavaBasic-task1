package org.ipr;


import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start");
        var logFileDivider =
                new LogFileDivider("src/main/resources/main.log", "Windows-1251", 10);
        logFileDivider.clearAllFilesFromDir();
        logFileDivider.divide();
        System.out.println("OK");

    }
}