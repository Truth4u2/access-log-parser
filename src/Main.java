import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {
        int validFileCount = 0;
        int totalRequests = 0;
        int yandexCount = 0;
        int googleCount = 0;
        Scanner scanner = new Scanner(System.in);

        Pattern userAgentPattern = Pattern.compile("\"([^\"]*)\"\\s*$");

        while (true) {
            System.out.print("Введите путь к файлу: ");
            String path = scanner.nextLine();

            File file = new File(path);

            if (!file.exists()) {
                System.out.println("Файл не существует.");
                continue;
            }
            if (file.isDirectory()) {
                System.out.println("Указанный путь ведёт к папке, а не к файлу.");
                continue;
            }

            Statistics stats = new Statistics();

            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int length = line.length();
                    if (length > 1024) {
                        throw new LineTooLongException("Обнаружена строка длиной более 1024 символов!");
                    }
                    totalRequests++;

                    try {
                        LogEntry logEntry = new LogEntry(line);
                        stats.addEntry(logEntry);

                        String[] parts = line.split("\"");
                        String originalUserAgent = (parts.length > 5) ? parts[5] : "-";

                        if (originalUserAgent.contains("YandexBot")) {
                            yandexCount++;
                        } else if (originalUserAgent.contains("Googlebot")) {
                            googleCount++;
                        }

                    } catch (Exception e) {
                    }
                }

                System.out.println("Общее количество запросов: " + totalRequests);
                System.out.printf("Доля запросов от YandexBot: %.2f%%\n", (totalRequests > 0 ? yandexCount * 100.0 / totalRequests : 0));
                System.out.printf("Доля запросов от Googlebot: %.2f%%\n", (totalRequests > 0 ? googleCount * 100.0 / totalRequests : 0));
                System.out.printf("Общий трафик: %d байт\n", stats.totalTraffic);
                System.out.printf("Текущая скорость трафика (байт/ч): %.2f\n", stats.getTrafficRate());

                stats.calculateAverages();

                System.out.printf("Среднее количество посещений сайта за час: %.2f\n", stats.getAverageVisitsPerHour());
                System.out.printf("Среднее количество ошибочных запросов в час: %.2f\n", stats.getAverageErrorsPerHour());
                System.out.printf("Средняя посещаемость одним пользователем: %.2f\n", stats.getAverageVisitsPerUser());
                System.out.println("Пиковое количество посещений сайта в секунду: " + stats.getPeakVisitsPerSecond());
                System.out.println("Максимальное количество посещений одним пользователем: " + stats.getMaxVisitsPerUser());

            } catch (LineTooLongException e) {
                System.out.println("Ошибка: " + e.getMessage());
                e.printStackTrace();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            List<String> pages200 = stats.getPages200();
            Statistics.printLimitedPages(pages200, 5);

            List<String> nonExistingPages = stats.getNonExistingPages();
            Statistics.printLimitedPages404(nonExistingPages, 5);

            HashMap<String, Double> osStats = stats.getOSUsage();
            System.out.println("Статистика по ОС:");
            for (String os : osStats.keySet()) {
                System.out.printf("%s: %.2f%%\n", os, osStats.get(os) * 100);
            }

            HashMap<String, Double> browserStats = stats.getBrowserUsage();
            System.out.println("Статистика по браузерам:");
            for (String browser : browserStats.keySet()) {
                System.out.printf("%s: %.2f%%\n", browser, browserStats.get(browser) * 100);
            }

            List<String> refererDomains = stats.getRefererDomains();
            System.out.println("Уникальные домены:");
            for (String domain : refererDomains) {
                System.out.println(domain);
            }

            validFileCount++;
            System.out.println("Путь указан верно");
            System.out.println("Это файл номер " + validFileCount);
        }
    }
}