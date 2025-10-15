import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent userAgent;

    public LogEntry(String logLine) {
        String[] parts = logLine.split("\"");
        String[] preParts = parts[0].trim().split(" ");
        this.ipAddr = preParts[0];
        int openBracket = logLine.indexOf('[');
        int closeBracket = logLine.indexOf(']');
        String dateStr = logLine.substring(openBracket + 1, closeBracket);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        this.time = LocalDateTime.ofInstant(java.time.ZonedDateTime.parse(dateStr, formatter).toInstant(), ZoneOffset.UTC);
        String requestStr = parts.length > 1 ? parts[1] : "";
        String[] requestParts = requestStr.trim().split(" ");
        this.method = HttpMethod.fromString(requestParts.length > 0 ? requestParts[0] : "");
        this.path = requestParts.length > 1 ? requestParts[1] : "-";
        String[] statusAndSize = (parts.length > 2 ? parts[2].trim().split(" ") : new String[0]);
        this.responseCode = statusAndSize.length > 0 ? Integer.parseInt(statusAndSize[0]) : -1;
        if (statusAndSize.length > 1 && !statusAndSize[1].equals("-")) {
            this.responseSize = Integer.parseInt(statusAndSize[1]);
        } else {
            this.responseSize = 0;
        }
        this.referer = parts.length > 3 ? parts[3] : "-";
        String userAgentStr = parts.length > 5 ? parts[5] : "-";
        this.userAgent = new UserAgent(userAgentStr);
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}