package org.ipr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogParser {
    private Path dirPath = null;
    private Path filterOutputDirPath = null;

    public void parseLogFromFile(Path filename, String charset, String regex) {
        // Формируем путь для выходного файла, убирая расширение .log и добавляя регулярное выражение
        Path outputFile = filterOutputDirPath.resolve(
                filename.getFileName().toString().replaceFirst("[.][^.]+$", "")
                        + " " + regex + ".log"
        );

        // Проверка существования директории и её создание, если необходимо
        if (!Files.exists(filterOutputDirPath)) {
            try {
                Files.createDirectories(filterOutputDirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (Stream<String> lines = getRegexLogStreamFromFile(filename, charset, regex);
             BufferedWriter writer = Files.newBufferedWriter(outputFile, Charset.forName(charset),
                     StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            // Записываем отфильтрованные строки в файл
            lines.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine(); // Переход на новую строку
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseLogFromFile(String filename, String charset, String regex) {
        parseLogFromFile(Paths.get(filename), charset, regex);
    }


    public void clearAllFilesFromFilterOutputDirPath() {
        LogFileDivider.clearAllFilesFromDirPath(filterOutputDirPath);
    }

    private Stream<String> getRegexLogStreamFromFile(Path filename, String charset, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Path logFile = dirPath.resolve(filename);
        try {
            // Фильтруем строки по регулярному выражению и возвращаем поток
            Stream<String> lines = Files.lines(logFile, Charset.forName(charset));
            return lines.filter(line -> pattern.matcher(line).find());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // CONSTRUCTORS AND GETTERS AND SETTERS ****************************************************************************
    public LogParser(String dirPath) {
        this(Paths.get(dirPath));
    }

    public LogParser(String dirPath, String filterOutputDirPath) {
        this(Paths.get(dirPath), Paths.get(filterOutputDirPath));
    }

    public LogParser(Path dirPath) {
        this.dirPath = dirPath;
        this.filterOutputDirPath = dirPath.resolve("filter-output");
    }

    public LogParser(Path dirPath, Path filterOutputDirPath) {
        this.dirPath = dirPath;
        this.filterOutputDirPath = filterOutputDirPath;
    }

    public static void main(String[] args) {
        System.out.println("Start");
    }
    // *****************************************************************************************************************
}
