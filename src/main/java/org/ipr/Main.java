package org.ipr;


import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        var logFileDivider = new LogParser.LogFileDivider("src/main/resources/main.log");
        logFileDivider.divide();
        System.out.println("OK");
    }
}