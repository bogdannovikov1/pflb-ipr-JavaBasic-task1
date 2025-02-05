package org.ipr;

import org.ipr.core.LogFileDivider;
import org.ipr.core.LogRegexParser;
import org.ipr.core.LogSeparatorInserter;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogParser {
    private Path logDir = null;
    private Path divideDir = null;
    private Path filterDir = null;
    private Charset charset = Charset.defaultCharset();

    private final String DEFAULT_DIVIDE_DIR = "my-divide";
    private final String DEFAULT_FILTER_DIR = "my-filter";

    // Разделяет файл logDir/logFileName на n файлов в и кладет их в divideDir
    // Возвращает маску выходных файлов
    public String divide(String logFileName, int n) {
        LogFileDivider divider = new LogFileDivider(
                this.logDir.resolve(logFileName),
                this.charset,
                n,
                divideDir,
                logFileName.replace(".", "-") + "-"
        );
        divider.divide();
        return divider.getFileNamePrefix()+"*";
    }

    // Парсит файлы, которые подходят под маску fileMask, по регулярному выражению regex
    // и кладет их в директорию filterDir
    // возвращает путь к выходному файлу
    public Path foundRegex(Path fromDir, String fileMask, String regex) {
        LogRegexParser parser = new LogRegexParser(fromDir, filterDir);
        String outFile = parser.parseLogFromFiles(fileMask, String.valueOf(this.charset), regex);
        if (outFile == null) {
            throw new RuntimeException("Files not found by mask: " + fileMask);
        }
        return filterDir.resolve(outFile);

    }

    // Перегрузка
    // По умолчанию аргумент fromDir = divideDir, если его не существует, то аргумент fromDir = logDir
    public Path foundRegex(String fileMask, String regex) {
        Path fromDir = this.logDir;
        if (Files.exists(divideDir)) {
            fromDir = this.divideDir;
        }
        return foundRegex(fromDir, fileMask, regex);
    }

    // Вставляет разделитель и создает CSV. Возвращает количество логов, которые были проигнорированы
    // Ищет файл filename в директории filterDir. Если там нет, то ищет в divideDir. Если там нет, то ищет в logDir
    public int insertSepatator(String separator, String fileName) {
        fileName = fileName.replace("*", "[AnyChars]").replace("?", "[OneChar]");
        Path file = this.logDir.resolve(fileName);
        if (Files.exists(filterDir.resolve(fileName))) {
            file = filterDir.resolve(fileName);
        } else if (Files.exists(divideDir.resolve(fileName))) {
            file = divideDir.resolve(fileName);
        }
        if (!Files.exists(file)) {
            throw new RuntimeException("File not found: " + file + " Используйте insertSepatator(String separator, Path file)");
        }
        return LogSeparatorInserter.insertAndMakeCSV(separator, filterDir.resolve(fileName), this.charset);
    }

    // Вставляет разделитель и создает CSV. Возвращает количество логов, которые были проигнорированы
    // file - конкретный файл
    public int insertSepatator(String separator, Path file) {
        return LogSeparatorInserter.insertAndMakeCSV(separator, file, this.charset);
    }

    public void clearDivideDir() {
        LogFileDivider.clearAllFilesFromDirPath(divideDir);
    }

    public void clearFilterDir() {
        LogFileDivider.clearAllFilesFromDirPath(filterDir);
    }

    // КОНСТРУКТОРЫ ГЕТТЕРЫ И СЕТТЕРЫ **********************************************************************************
    public LogParser(String logDir) {
        this.logDir = Paths.get(logDir);
        this.divideDir = this.logDir.resolve(DEFAULT_DIVIDE_DIR);
        this.filterDir = this.logDir.resolve(DEFAULT_FILTER_DIR);
    }

    public LogParser(String logDir, Charset charset) {
        this(logDir);
        this.charset = charset;
    }

    public LogParser(String logDir, String charset) {
        this(logDir, Charset.forName(charset));
    }

    public Path getLogDir() {
        return logDir;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Path getDivideDir() {
        return divideDir;
    }

    public void setDivideDir(Path divideDir) {
        this.divideDir = divideDir;
    }

    public Path getFilterDir() {
        return filterDir;
    }

    public void setFilterDir(Path filterDir) {
        this.filterDir = filterDir;
    }

    @Override
    public String toString() {
        return "LogParser{" +
                "logDir=" + logDir +
                ", charset=" + charset +
                ", divideDir=" + divideDir +
                ", filterDir=" + filterDir +
                '}';
    }
    // *****************************************************************************************************************
}
