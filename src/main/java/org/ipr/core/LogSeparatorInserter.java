package org.ipr.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ipr.core.FinalLogRegex.*;

public class LogSeparatorInserter {
    private Path csvFile = null;
    private Path ignoredFile = null;


    // Вставляет разделитель и создает CSV в том же каталоге
    // Возвращает количество логов, которые были проигнорированы
    public int insertAndMakeCSV(String separator, Path toFile, Charset charset) {
        int null_counter = 0;
        Path CSVfilename = Paths.get(
                toFile.toString().replaceFirst("[.][^.]+$", "") + ".csv"
        );
        Path ignoredFilename = Paths.get(
                toFile.toString().replaceFirst("[.][^.]+$", "") + ".ignored"
        );
        try (
                BufferedReader reader = Files.newBufferedReader(toFile, charset);
                BufferedWriter writer = Files.newBufferedWriter(CSVfilename, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                BufferedWriter writer_to_ignored = Files.newBufferedWriter(ignoredFilename, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String separatedLine = getSeparatedLogLine(line, separator);
                if (separatedLine != null) {
                    writer.write(separatedLine);
                    writer.newLine();
                } else {
                    writer_to_ignored.write(line);
                    writer_to_ignored.newLine();
                    null_counter++;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (null_counter == 0) {
            // Удалить файл
            if (Files.exists(ignoredFilename)) {
                try {
                    Files.delete(ignoredFilename);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.csvFile = CSVfilename;
        this.ignoredFile = ignoredFilename;
        return null_counter;
    }

    // Основной метод в котором происходит разделение логов
    // В зависимости от статуса лога, вызывается соответствующий код,
    // возвращается уже строка форматированного лога
    private String getSeparatedLogLine(String line, String separator) {
        String status = getLogStatus(line);
        if (status == null) {
            return null;
        }
        LogStatus logStatus = LogStatus.valueOf(status);
        String regex = null;
        Pattern pattern = null;
        Matcher matcher = null;
        switch (logStatus) {
            case TRACE:
                pattern = Pattern.compile(TRACE_REGEX);
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String dateTime = matcher.group(1);
                    String trace = matcher.group(2);
                    String ipPort = matcher.group(3) + "." + matcher.group(4);
                    String rest = matcher.group(5);
                    // Форматированный вывод
                    return String.join("|", dateTime, trace.replace(":", ""), ipPort, rest);
                } else {
                    return null;
                }
            case DEBUG:
                pattern = Pattern.compile(DEBAG_REGEX);
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String dateTime = matcher.group(1);
                    String debug = matcher.group(2);
                    String ipAndPort = matcher.group(3);
                    String message = matcher.group(4);
                    // Вывод результата с разделителем |
                    return String.join("|", dateTime, debug.replace(":", ""), ipAndPort, message);
                } else {
                    return null;
                }
            case INFO:
                pattern = Pattern.compile(INFO_REGEX);
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String date = matcher.group(1);   // Дата и время
                    String level = matcher.group(2);  // Уровень логирования ("INFO")
                    String ip = matcher.group(3);     // IP, если найден; иначе null
                    String message = matcher.group(4); // Сообщение

                    // Если в группе с IP ничего не захвачено, устанавливаем "null"
                    if (ip == null || ip.isEmpty()) {
                        // Регулярное выражение для поиска IP (с портом, если имеется) в теле сообщения
                        Pattern ipPattern = Pattern.compile("(\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d+)?(?:\\.\\w+)?)");
                        Matcher ipMatcher = ipPattern.matcher(message);
                        if (ipMatcher.find()) {
                            ip = ipMatcher.group(1);
                        } else {
                            ip = "null";
                        }
                    }
                    return String.join("|", date, level.replace(":", ""), ip, message);
                } else {
                    return null;
                }
            case WARN:
                pattern = Pattern.compile(WARN_REGEX);
                matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String dateTime = matcher.group(1);
                    String logLevel = matcher.group(2);
                    String ipAndPort = matcher.group(3);
                    String message = matcher.group(4);
                    // Вывод результата с разделителем |
                    return String.join("|", dateTime, logLevel.replace(":", ""), ipAndPort, message);
                } else {
                    return null;
                }

            default:
                return null;
        }
    }

    // Вспомогательный метод для получения статуса лога
    private String getLogStatus(String line) {
        Pattern pattern = Pattern.compile(FOUND_LOG_STATUS_REGEX);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // Перегрузка
    public int insertAndMakeCSV(String separator, String toFile, String charset) {
        return insertAndMakeCSV(separator, Paths.get(toFile), Charset.forName(charset));
    }

    public Path getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(Path csvFile) {
        this.csvFile = csvFile;
    }

    public Path getIgnoredFile() {
        return ignoredFile;
    }

    public void setIgnoredFile(Path ignoredFile) {
        this.ignoredFile = ignoredFile;
    }
}
