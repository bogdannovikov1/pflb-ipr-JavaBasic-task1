package org.ipr;

import org.ipr.core.LogFileDivider;
import org.ipr.core.LogRegexParser;
import org.ipr.core.LogSeparatorInserter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogParser {
    private Path logDir = null;
    private Path divideDir = null;
    private Path filterDir = null;
    private Charset charset = Charset.defaultCharset();
    private boolean help = false;

    private final String DEFAULT_DIVIDE_DIR = "my-batches";
    private final String DEFAULT_FILTER_DIR = "my-filter";


    public String divide(String logFileName, int n, Path toDir, String filesPrefix) {
        if (help) {
            System.out.println("Начинаю разделение " + logFileName + " на " + n + " частей ...");
        }
        setDivideDir(toDir);
        LogFileDivider divider = new LogFileDivider(
                logDir.resolve(logFileName),
                this.charset,
                n,
                toDir,
                filesPrefix
        );
        divider.divide();
        if (help) {
            System.out.println("Разделение " + logFileName + " на " + n + " частей завершено. Выходные файлы: " +
                    divideDir + File.separator + divider.getFileNamePrefix() + "*");
        }
        return divider.getFileNamePrefix() + "*";
    }


    // Перегрузка
    public String divide(String logFileName, int n) {
        return divide(logFileName,
                n,
                divideDir,
                logFileName.replace(".", "-") + "-"
        );
    }

    // Парсит файлы, которые подходят под маску fileMask, по регулярному выражению regex
    // и кладет их в директорию filterDir
    // возвращает путь к выходному файлу
    public Path foundRegex(Path fromDir, String fileMask, String regex) {
        if (help) {
            System.out.println("Поиск логов по регулярному выражению " + regex + " в директории " + fromDir + " среди файлов по маске " + fileMask + " ...");
        }
        LogRegexParser parser = new LogRegexParser(fromDir, filterDir);
        String outFile = parser.parseLogFromFiles(fileMask, String.valueOf(this.charset), regex);
        if (outFile == null) {
            throw new RuntimeException("Files not found by mask: " + fileMask);
        }
        // Если файл пустой
        try {
            if (Files.size(filterDir.resolve(outFile)) == 0) {
                Files.delete(filterDir.resolve(outFile));
                throw new RuntimeException("No lines found by regex: " + regex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (help) {
            System.out.println("Готово. Результат в: " + filterDir.resolve(outFile));
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
    // file - конкретный файл
    public int insertSepatator(String separator, Path file) {
        if (help) {
            System.out.println("Вставляю разделитель " + separator + " в " + file + " ...");
        }
        LogSeparatorInserter inserter = new LogSeparatorInserter();
        int c = inserter.insertAndMakeCSV(separator, file, this.charset);
        if (help) {
            System.out.print("Готово. Результат в: " + inserter.getCsvFile() + " Проигнорировано логов: " + c);
            if (c > 0) {
                System.out.print(" (см. " + inserter.getIgnoredFile() + " файл в той же директории)");
                System.out.println(" если логи не матчатся, то можете попробовать отредактировать регулярные выражения " +
                        "в src/main/java/org/ipr/core/FinalLogRegex.java");
                System.out.println("предполагается делить логи на 4 колонки регулярными выражениями с 4мя группами, " +
                        "если колонок больше то нужно менять логику в src/main/java/org/ipr/core/LogSeparatorInserter.java");
            }
            System.out.println();
        }
        return c;
    }

    //Перегрузка
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
        return insertSepatator(separator, file);
    }

    public void clearDivideDir() {
        if (help) {
            System.out.println("Очищаю файлы в директории " + divideDir + " если они там есть");
        }
        LogFileDivider.clearAllFilesFromDirPath(divideDir);
    }

    public void clearFilterDir() {
        if (help) {
            System.out.println("Очищаю файлы в директории " + filterDir + " если они там есть");
        }
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

    public LogParser(Path logDir) {
        this.logDir = logDir;
        this.divideDir = this.logDir.resolve(DEFAULT_DIVIDE_DIR);
        this.filterDir = this.logDir.resolve(DEFAULT_FILTER_DIR);
    }

    public LogParser(Path logDir, Charset charset) {
        this(logDir);
        this.charset = charset;
    }

    public LogParser(Path logDir, String charset) {
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

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    @Override
    public String toString() {
        return "LogParser{" +
                "logDir=" + logDir +
                ", divideDir=" + divideDir +
                ", filterDir=" + filterDir +
                ", charset=" + charset +
                ", printLogs=" + help +
                ", DEFAULT_DIVIDE_DIR='" + DEFAULT_DIVIDE_DIR + '\'' +
                ", DEFAULT_FILTER_DIR='" + DEFAULT_FILTER_DIR + '\'' +
                '}';
    }

    // *****************************************************************************************************************
}
