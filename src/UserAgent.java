public class UserAgent {
    private final String os;
    private final String browser;

    public UserAgent(String userAgentStr) {
        this.os = parseOS(userAgentStr);
        this.browser = parseBrowser(userAgentStr);
    }

    private String parseOS(String ua) {
        String uaLower = ua.toLowerCase();
        if (uaLower.contains("windows")) {
            return "Windows";
        } else if (uaLower.contains("macintosh") || uaLower.contains("mac os")) {
            return "macOS";
        } else if (uaLower.contains("linux")) {
            return "Linux";
        } else {
            return "Other";
        }
    }

    private String parseBrowser(String ua) {
        String uaLower = ua.toLowerCase();
        if (uaLower.contains("edge")) {
            return "Edge";
        } else if (uaLower.contains("firefox")) {
            return "Firefox";
        } else if (uaLower.contains("chrome")) {
            return "Chrome";
        } else if (uaLower.contains("opera")) {
            return "Opera";
        } else {
            return "Other";
        }
    }

    public String getOs() {
        return os;
    }

    public String getBrowser() {
        return browser;
    }
}