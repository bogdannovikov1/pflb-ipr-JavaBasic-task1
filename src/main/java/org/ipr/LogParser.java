package org.ipr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogParser {


    public static class LogFileDivider {
        private int numOfFiles = 5;
        private Path dirPath = Paths.get("src/main/resources/mylogs");
        private String fileNamePrefix = "my-log-";
        private Path fromFile = null;

        public void divide() {
            try {
                // Получаем общее количество строк в файле
                long countLines = countLines(fromFile);
                long maxBatchSize = countLines / numOfFiles;  // Максимальное количество строк в одном файле

                try (BufferedReader reader = Files.newBufferedReader(fromFile)) {
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

        public void divideFrom(Path fromFile) {
            setFromFile(fromFile);
            divide();
        }

        public void divideFrom(String fromFile) {
            setFromFile(Paths.get(fromFile));
            divide();
        }

        private void writeToFile(List<String> lines, int index) {
            Path newFilePath = dirPath.resolve(fileNamePrefix + index);
            try {
                Files.createDirectories(newFilePath.getParent());  // Создаем родительскую директорию, если ее нет
                Files.write(newFilePath, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE);  // Записываем строки в файл
            } catch (IOException e) {
                throw new RuntimeException("Error writing to file: " + newFilePath, e);
            }
        }

        private long countLines(Path path) throws IOException {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                return reader.lines().count();
            }
        }

        public LogFileDivider(Path fromFile) {
            this.fromFile = fromFile;
        }

        public LogFileDivider(Path fromFile, int numOfFiles) {
            this.fromFile = fromFile;
            this.numOfFiles = numOfFiles;
        }

        public LogFileDivider(Path fromFile, int numOfFiles, Path dirPath, String fileNamePrefix) {
            this.fromFile = fromFile;
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

        @Override
        public String toString() {
            return "logFileDivider{" +
                    "numOfFiles=" + numOfFiles +
                    ", dirPath=" + dirPath +
                    ", fileNamePrefix='" + fileNamePrefix + '\'' +
                    ", fromFile=" + fromFile +
                    '}';
        }
    }
}
