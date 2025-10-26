import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Statistics {
    long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> pages;
    private HashMap<String, Integer> osCount;
    private HashSet<String> nonExistingPages;
    private HashMap<String, Integer> browserCount;

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
        pages = new HashSet<>();
        osCount = new HashMap<>();
        nonExistingPages = new HashSet<>();
        browserCount = new HashMap<>();
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
}