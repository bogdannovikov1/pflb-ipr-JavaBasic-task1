package org.ipr;


import java.nio.file.Path;

public class Test1 {
    public static void main(String[] args) {
        System.out.println("Start");
        LogParser logParser = new LogParser("src/main/resources/", "Windows-1251");
        logParser.clearDivideDir();
        logParser.clearFilterDir();

        String mainDividedMask = logParser.divide("main.log", 10);
        Path traceFilterFile = logParser.foundRegex(mainDividedMask, "TRACE");
        int ignored = logParser.insertSepatator("|", traceFilterFile);
        System.out.println(ignored);
        System.out.println("End");
    }
}