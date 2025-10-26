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

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
        pages = new HashSet<>();
        osCount = new HashMap<>();
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

        if (entry.getResponseCode() == 200) {
            pages.add(entry.getPath());
        }

        String os = entry.getUserAgent().getOs();
        osCount.put(os, osCount.getOrDefault(os, 0) + 1);
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

    public List<String> getPages() {
        return new ArrayList<>(pages);
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

    public HashMap<String, Double> getOSUsage() {
        HashMap<String, Double> osUsage = new HashMap<>();
        int totalOSCount = osCount.values().stream().mapToInt(Integer::intValue).sum();
        if (totalOSCount == 0) {
            return osUsage; // Пустой, если данных нет
        }
        for (String os : osCount.keySet()) {
            double share = (double) osCount.get(os) / totalOSCount;
            osUsage.put(os, share);
        }
        return osUsage;
    }
}