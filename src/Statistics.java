import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class Statistics {
    long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> pages;
    private HashMap<String, Integer> osCount;
    private HashSet<String> nonExistingPages;
    private HashMap<String, Integer> browserCount;
    private double averageVisitsPerHour;
    private double averageErrorsPerHour;
    private double averageVisitsPerUser;
    private int errorCount;
    private HashSet<String> userIPs;
    private int totalVisits;
    private HashMap<Long, Integer> visitsPerSecond;
    private HashSet<String> referers;
    private HashMap<String, Integer> userVisitCounts;

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
        pages = new HashSet<>();
        osCount = new HashMap<>();
        nonExistingPages = new HashSet<>();
        browserCount = new HashMap<>();
        errorCount = 0;
        userIPs = new HashSet<>();
        totalVisits = 0;
        averageVisitsPerHour = 0;
        averageErrorsPerHour = 0;
        averageVisitsPerUser = 0;
        visitsPerSecond = new HashMap<>();
        referers = new HashSet<>();
        userVisitCounts = new HashMap<>();
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();

        LocalDateTime time = entry.getTime();

        if (minTime == null || time.isBefore(minTime)) {
            minTime = time;
        }
        if (maxTime == null || time.isAfter(maxTime)) {
            maxTime = time;
        }

        int responseCode = entry.getResponseCode();

        if (responseCode == 200) {
            pages.add(entry.getPath());
        } else if (responseCode == 404) {
            nonExistingPages.add(entry.getPath());
        }

        String os = entry.getUserAgent().getOs();
        osCount.put(os, osCount.getOrDefault(os, 0) + 1);

        String browser = entry.getUserAgent().getBrowser();
        browserCount.put(browser, browserCount.getOrDefault(browser, 0) + 1);

        if (responseCode >= 400 && responseCode < 600) {
            errorCount++;
        }

        String userAgentStr = entry.getUserAgent().getOriginalUserAgent();
        boolean isBot = userAgentStr == null || userAgentStr.toLowerCase().contains("bot");
        String ip = entry.getIpAddr();

        if (!isBot && ip != null) {
            userIPs.add(ip);
            totalVisits++;
            long epochSecond = entry.getTime().toEpochSecond(ZoneOffset.UTC);
            visitsPerSecond.put(epochSecond, visitsPerSecond.getOrDefault(epochSecond, 0) + 1);
            userVisitCounts.put(ip, userVisitCounts.getOrDefault(ip, 0) + 1);
        }

        String referer = entry.getReferer();
        if (referer != null && !referer.equals("-")) {
            referers.add(referer);
        }
    }

    public void calculateAverages() {
        if (minTime == null || maxTime == null) {
            this.averageVisitsPerHour = 0;
            this.averageErrorsPerHour = 0;
            this.averageVisitsPerUser = 0;
            return;
        }

        long hoursBetween = Duration.between(minTime, maxTime).toHours();
        if (hoursBetween == 0) {
            hoursBetween = 1;
        }

        this.averageVisitsPerHour = (double) totalVisits / hoursBetween;
        this.averageErrorsPerHour = (double) errorCount / hoursBetween;

        int uniqueUsers = userIPs.size();
        this.averageVisitsPerUser = (uniqueUsers > 0) ? (double) totalVisits / uniqueUsers : 0;
    }

    public double getAverageVisitsPerHour() {
        return averageVisitsPerHour;
    }

    public double getAverageErrorsPerHour() {
        return averageErrorsPerHour;
    }

    public double getAverageVisitsPerUser() {
        return averageVisitsPerUser;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0.0;
        }
        long hoursBetween = java.time.Duration.between(minTime, maxTime).toHours();
        if (hoursBetween == 0) {
            hoursBetween = 1;
        }
        return (double) totalTraffic / hoursBetween;
    }

    public List<String> getPages200() {
        return new ArrayList<>(pages);
    }

    public List<String> getNonExistingPages() {
        return new ArrayList<>(nonExistingPages);
    }

    public static void printLimitedPages(List<String> pages, int limit) {
        int total = pages.size();
        int toShow = Math.min(total, limit);
        System.out.println("Первые " + toShow + " страниц с кодом ответа 200:");
        for (int i = 0; i < toShow; i++) {
            System.out.println(pages.get(i));
        }
        if (total > limit) {
            System.out.println("И еще " + (total - limit) + " страниц с кодом ответа 200");
        }
    }

    public static void printLimitedPages404(List<String> pages, int limit) {
        int total = pages.size();
        int toShow = Math.min(total, limit);
        System.out.println("Первые " + toShow + " страниц с кодом ответа 404:");
        for (int i = 0; i < toShow; i++) {
            System.out.println(pages.get(i));
        }
        if (total > limit) {
            System.out.println("И еще " + (total - limit) + " страниц с кодом ответа 404");
        }
    }

    public HashMap<String, Double> getOSUsage() {
        HashMap<String, Double> osUsage = new HashMap<>();
        int totalOSCount = osCount.values().stream().mapToInt(Integer::intValue).sum();
        if (totalOSCount == 0) {
            return osUsage;
        }
        for (String os : osCount.keySet()) {
            double share = (double) osCount.get(os) / totalOSCount;
            osUsage.put(os, share);
        }
        return osUsage;
    }

    public HashMap<String, Double> getBrowserUsage() {
        HashMap<String, Double> browserUsage = new HashMap<>();
        int totalBrowsersCount = 0;
        if (browserCount != null) {
            totalBrowsersCount = browserCount.values().stream().mapToInt(Integer::intValue).sum();
        }
        if (totalBrowsersCount == 0) {
            return browserUsage;
        }
        for (String browser : browserCount.keySet()) {
            double share = (double) browserCount.get(browser) / totalBrowsersCount;
            browserUsage.put(browser, share);
        }
        return browserUsage;
    }

    public int getPeakVisitsPerSecond() {
        int maxVisits = 0;
        for (Map.Entry<Long, Integer> entry : visitsPerSecond.entrySet()) {
            if (entry.getValue() > maxVisits) {
                maxVisits = entry.getValue();
            }
        }
        return maxVisits;
    }

    public List<String> getRefererDomains() {
        Set<String> domains = new HashSet<>();
        for (String referer : referers) {
            try {
                URL url = new URL(referer);
                String host = url.getHost();
                if (host.startsWith("www.")) {
                    host = host.substring(4);
                }
                domains.add(host);
            } catch (MalformedURLException e) {
            }
        }
        return new ArrayList<>(domains);
    }

    public int getMaxVisitsPerUser() {
        int maxVisits = 0;
        for (int count : userVisitCounts.values()) {
            if (count > maxVisits) {
                maxVisits = count;
            }
        }
        return maxVisits;
    }
}