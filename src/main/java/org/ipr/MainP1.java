package org.ipr;


import java.nio.file.Path;
import java.nio.file.Paths;


//п.1.  2 параметра: <путь/имя_лог_файла>,<путь/постоянная_часть_имени_полученных_файлов>
//п.2.  а) 3 параметра: <фраза_поиска>,<путь_до_лог_файла(ов)>,<имя_нового_файла>
//б) 3 параметра: <тип_разделителя>,<путь_до_лог_файла(ов)>,<имя_нового_файла>
//п.3. 1 параметр: <--help>

public class MainP1 {
    public static void main(String[] args) {
        // П1
        if (args.length < 2) {
            throw new RuntimeException("Not enough arguments");
        }
        Path logFile = Paths.get(args[0]);
        Path outputDir = Paths.get(args[1]).getParent();
        String filesPrefix = Paths.get(args[1]).getFileName().toString();

        boolean help = false;
        if (args.length == 3) {
            help = args[2].equals("--help");
        }

        LogParser logParser = new LogParser(logFile.getParent(), "Windows-1251");

        logParser.setHelp(help);

        logParser.divide(logFile.getFileName().toString(), 10, outputDir, filesPrefix);
    }
}