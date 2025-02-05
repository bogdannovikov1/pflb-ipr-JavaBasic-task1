package org.ipr;

//п.1.  2 параметра: <путь/имя_лог_файла>,<путь/постоянная_часть_имени_полученных_файлов>
//п.2.  а) 3 параметра: <фраза_поиска>,<путь_до_лог_файла(ов)>,<имя_нового_файла>
//б) 3 параметра: <тип_разделителя>,<путь_до_лог_файла(ов)>,<имя_нового_файла>
//п.3. 1 параметр: <--help>

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MainP2 {
    public static void main(String[] args) {
        // П2
        if (args.length < 3) {
            throw new RuntimeException("Not enough arguments");
        }
        String regex = args[0];
        Path logsPath = Paths.get(args[1]);
        String newFileName = args[2];

        boolean help = false;
        if (args.length == 4) {
            help = args[3].equals("--help");
        }

        // Если logPath это директория
        LogParser logParser = null;
        Path file = null;
        if (Files.isDirectory(logsPath)) {
            logParser = new LogParser(logsPath, "Windows-1251");
            logParser.setHelp(help);
            file = logParser.foundRegex(logsPath, "*", regex);
        } else if (Files.isRegularFile(logsPath)) {
            // Если logPath это конкретный файл
            logParser = new LogParser(logsPath.getParent(), "Windows-1251");
            logParser.setHelp(help);
            file = logParser.foundRegex(logsPath.getFileName(), String.valueOf(logsPath), regex);
        }


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.move(file, logsPath.resolve(newFileName), StandardCopyOption.REPLACE_EXISTING);
            if (help) {
                System.out.println("Файл " + file.getFileName() + " переименован в " + logsPath.resolve(newFileName).getFileName());
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
