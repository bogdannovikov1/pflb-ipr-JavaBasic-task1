package org.ipr.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogFileDivider {
    private int numOfFiles = 5;
    private Path dirPath = null;
    private String fileNamePrefix = "my-log-";
    private Path fromFile = null;
    private Charset charset = StandardCharsets.UTF_8;

    // Разделить лог-файл fromFile на части (numOfFiles) и сохранить в директории dirPath
    public void divide() {
        try {
            // Получаем общее количество строк в файле
            long countLines = countLines(fromFile);
            long maxBatchSize = countLines / numOfFiles;  // Максимальное количество строк в одном файле

            try (BufferedReader reader = Files.newBufferedReader(fromFile, charset)) {
                List<String> batch = new ArrayList<>();
                String line;
                int currentLine = 0;
                int fileIndex = 0;

                // Читаем файл построчно и записываем в части
                while ((line = reader.readLine()) != null) {
                    batch.add(line);  // Добавляем строку в текущую партию
                    currentLine++;

                    // Если текущая партия полна, записываем ее в файл
                    if (currentLine >= maxBatchSize) {
                        String fileName = fileNamePrefix + fileIndex + ".log";
                        writeToFile(batch, fileIndex);
                        fileIndex++;
                        batch.clear();  // Очищаем партию для следующего набора строк
                        currentLine = 0;
                    }
                }

                // Записываем оставшиеся строки, если они есть
                if (!batch.isEmpty()) {
                    writeToFile(batch, fileIndex);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Перегрузка
    public void divideFrom(Path fromFile) {
        setFromFile(fromFile);
        setDirPath(fromFile.getParent().resolve("my-logs"));
        divide();
    }

    // Перегрузка
    public void divideFrom(String fromFile) {
        setFromFile(Paths.get(fromFile));
        setDirPath(this.fromFile.getParent().resolve("my-logs"));
        divide();
    }

    // Удалить все файлы из директории dirPath
    public void clearAllFilesFromDirPath() {
        clearAllFilesFromDirPath(dirPath);
    }

    // Статический вспомогательный метод очистки файлов из директории (не рекурсивная)
    public static void clearAllFilesFromDirPath(Path dirPath) {
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            return;
        }
        try (Stream<Path> files = Files.list(dirPath)) { // Используем Files.list для первого уровня файлов
            files
                    .filter(Files::isRegularFile) // Фильтруем только файлы, игнорируя директории
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.delete()) {
                            throw new RuntimeException("Error deleting: " + file);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Error processing directory: " + dirPath, e);
        }
    }

    // Вспомогательный метод
    private String writeToFile(List<String> lines, int index) {
        Path newFilePath = dirPath.resolve(fileNamePrefix + index + ".log");
        try {
            Files.createDirectories(newFilePath.getParent());  // Создаем родительскую директорию, если ее нет
            Files.write(newFilePath, lines, charset, StandardOpenOption.CREATE, StandardOpenOption.WRITE);  // Записываем строки в файл
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + newFilePath, e);
        }
        return newFilePath.toString();
    }

    // Вспомогательный метод
    private long countLines(Path path) throws IOException {
        long c = 0;
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            while (reader.readLine() != null) {
                c++;
            }
        }
        return c;
    }

    // CONSTRUCTORS AND GETTERS AND SETTERS ****************************************************************************
    public LogFileDivider(String fromFile) {
        this(Paths.get(fromFile));
    }

    public LogFileDivider(String fromFile, String charset) {
        this(Paths.get(fromFile), Charset.forName(charset));
    }

    public LogFileDivider(String fromFile, String charset, int numOfFiles) {
        this(Paths.get(fromFile), Charset.forName(charset), numOfFiles);
    }

    public LogFileDivider(String fromFile, String charset, int numOfFiles, String dirPath, String fileNamePrefix) {
        this(Paths.get(fromFile), Charset.forName(charset), numOfFiles, Paths.get(dirPath), fileNamePrefix);
    }

    public LogFileDivider(Path fromFile) {
        this.fromFile = fromFile;
        this.dirPath = fromFile.getParent().resolve("my-logs");
    }

    public LogFileDivider(Path fromFile, Charset charset) {
        this.fromFile = fromFile;
        this.dirPath = fromFile.getParent().resolve("my-logs");
        this.charset = charset;
    }

    public LogFileDivider(Path fromFile, Charset charset, int numOfFiles) {
        this.fromFile = fromFile;
        this.dirPath = fromFile.getParent().resolve("my-logs");
        this.charset = charset;
        this.numOfFiles = numOfFiles;
    }

    public LogFileDivider(Path fromFile, Charset charset, int numOfFiles, Path dirPath, String fileNamePrefix) {
        this.fromFile = fromFile;
        this.charset = charset;
        this.numOfFiles = numOfFiles;
        this.dirPath = dirPath;
        this.fileNamePrefix = fileNamePrefix;
    }


    public int getNumOfFiles() {
        return numOfFiles;
    }

    public void setNumOfFiles(int numOfFiles) {
        this.numOfFiles = numOfFiles;
    }

    public Path getDirPath() {
        return dirPath;
    }

    public void setDirPath(Path dirPath) {
        this.dirPath = dirPath;
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public Path getFromFile() {
        return fromFile;
    }

    public void setFromFile(Path fromFile) {
        this.fromFile = fromFile;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String toString() {
        return "LogFileDivider{" +
                "numOfFiles=" + numOfFiles +
                ", dirPath=" + dirPath +
                ", fileNamePrefix='" + fileNamePrefix + '\'' +
                ", fromFile=" + fromFile +
                ", charset=" + charset +
                '}';
    }
    // *****************************************************************************************************************
}
