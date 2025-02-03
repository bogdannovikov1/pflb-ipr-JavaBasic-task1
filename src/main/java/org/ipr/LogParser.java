package org.ipr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogParser {
    private Path dirPath = null;
    private Path filterOutputDirPath = null;

    public void parseLogFromFiles(String filePattern, String charset, String regex) {
        // Формируем путь для выходного файла, убирая расширение .log и добавляя регулярное выражение
        Path outputFile = filterOutputDirPath.resolve(
                filePattern.replaceFirst("[.][^.]+$", "")
                        .replace("*", "[AnyChars]")
                        .replace("?", "[AnyChar]")
                        + "@" + safeRegex(regex) + ".log"
        );
        // Проверка существования директории и её создание, если необходимо
        addFilterOutputDir();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, filePattern)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    // Здесь можно обработать файл
                    Stream<String> trueLines = getRegexLogStreamFromFile(entry, charset, regex);
                    try (BufferedWriter writer = Files.newBufferedWriter(outputFile, Charset.forName(charset),
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                        // Записываем отфильтрованные строки в файл
                        trueLines.forEach(line -> {
                            try {
                                writer.write(line);
                                writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    System.out.println("Processing file [OK] " + entry);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Перегрузка
    public void parseLogFromFiles(String filePattern, String regex) {
        parseLogFromFiles(filePattern, "UTF-8", regex);
    }

    public void parseLogFromFile(Path filename, String charset, String regex) {
        // Формируем путь для выходного файла, убирая расширение .log и добавляя регулярное выражение
        Path outputFile = filterOutputDirPath.resolve(
                filename.getFileName().toString().replaceFirst("[.][^.]+$", "")
                        + "@" + safeRegex(regex) + ".log"
        );
        // Проверка существования директории и её создание, если необходимо
        addFilterOutputDir();

        try (Stream<String> lines = getRegexLogStreamFromFile(dirPath.resolve(filename), charset, regex);
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

    // Перегрузка
    public void parseLogFromFile(String filename, String charset, String regex) {
        parseLogFromFile(Paths.get(filename), charset, regex);
    }

    // Перегрузка
    public void parseLogFromFile(Path filename, String regex) {
        parseLogFromFile(filename, "UTF-8", regex);
    }

    // Перегрузка
    public void parseLogFromFile(String filename, String regex) {
        parseLogFromFile(Paths.get(filename), regex);
    }


    public void clearAllFilesFromFilterOutputDirPath() {
        LogFileDivider.clearAllFilesFromDirPath(filterOutputDirPath);
    }

    private Stream<String> getRegexLogStreamFromFile(Path filename, String charset, String regex) {
        Pattern pattern = Pattern.compile(regex);
        try {
            // Фильтруем строки по регулярному выражению и возвращаем поток
            Stream<String> lines = Files.lines(filename, Charset.forName(charset));
            return lines.filter(line -> pattern.matcher(line).find());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addFilterOutputDir() {
        // Проверка существования директории и её создание, если необходимо
        if (!Files.exists(filterOutputDirPath)) {
            try {
                Files.createDirectories(filterOutputDirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String safeRegex(String regex) {
        // Заменяем символы, которые могут быть запрещены в имени файла, на допустимые
        // Заменяем запрещенные символы на _
        // Заменяем точку на _

        return regex
                .replaceAll("[\\\\/:*?\"<>|]", "_")  // Заменяем запрещенные символы на _
                .replaceAll("\\.", "_");
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
