package org.ipr;

//п.1.  2 параметра: <путь/имя_лог_файла>,<путь/постоянная_часть_имени_полученных_файлов>
//п.2.  а) 3 параметра: <фраза_поиска>,<путь_до_лог_файла(ов)>,<имя_нового_файла>
//б) 2 параметра: <тип_разделителя>,<путь_до_лог_файла(ов)>
//п.3. 1 параметр: <--help>

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class MainP3 {
    public static void main(String[] args) {
        // П2
        System.out.println(Arrays.toString(args));
        if (args.length < 2) {
            throw new RuntimeException("Not enough arguments");
        }
        String separator = args[0];
        Path logsPath = Paths.get(args[1]);

        boolean help = false;
        if (args.length == 3) {
            help = args[2].equals("--help");
        }

        // Если logPath это директория
        LogParser logParser = null;
        if (Files.isDirectory(logsPath)) {
            // Для всех фалов в директории
            logParser = new LogParser(logsPath, "Windows-1251");
            logParser.setHelp(help);
            for (File file : Objects.requireNonNull(logsPath.toFile().listFiles())) {
                int ignored = logParser.insertSepatator(separator, Paths.get(file.getPath()));
            }
        } else if (Files.isRegularFile(logsPath)) {
            // Если logPath это конкретный файл
            logParser = new LogParser(logsPath.getParent(), "Windows-1251");
            logParser.setHelp(help);
            int ignored = logParser.insertSepatator(separator, logsPath);
        }
    }

}
