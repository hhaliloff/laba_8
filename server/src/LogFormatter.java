import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogFormatter extends Formatter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN  = "\u001B[32m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GRAY   = "\u001B[37m";

    @Override
    public String format(LogRecord record) {
        String time = LocalDateTime.now().format(DATE_FORMAT);
        String level = coloredLevel(record.getLevel().getName());
        String message = formatMessage(record);

        String result = String.format("[%s] %s %s%n", time, level, message);

        if (record.getThrown() != null) {
            result += RED + formatException(record.getThrown()) + RESET + "\n";
        }

        return result;
    }

    private String coloredLevel(String level) {
        return switch (level) {
            case "SEVERE"  -> RED    + "[ОШИБКА]" + RESET;
            case "WARNING" -> YELLOW + "[WARN]  " + RESET;
            case "INFO"    -> GREEN  + "[INFO]  " + RESET;
            case "FINE"    -> CYAN   + "[DEBUG] " + RESET;
            default        -> GRAY   + "[" + level + "]" + RESET;
        };
    }

    private String formatException(Throwable thrown) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(thrown.getClass().getName()).append(": ").append(thrown.getMessage()).append("\n");
        for (StackTraceElement el : thrown.getStackTrace()) {
            sb.append("    at ").append(el).append("\n");
        }
        return sb.toString();
    }
}