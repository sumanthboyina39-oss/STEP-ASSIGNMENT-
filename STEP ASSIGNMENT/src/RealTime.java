import java.util.*;

class RealTime {
    private HashMap<String, Integer> pageViews = new HashMap<>();
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(String url, String userId, String source) {

        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    public List<Map.Entry<String, Integer>> getTopPages() {

        List<Map.Entry<String, Integer>> list =
                new ArrayList<>(pageViews.entrySet());

        list.sort((a, b) -> b.getValue() - a.getValue());

        return list.subList(0, Math.min(10, list.size()));
    }

    public void getDashboard() {

        System.out.println("\n===== REAL-TIME DASHBOARD =====\n");

        System.out.println("Top Pages:");

        int rank = 1;

        for (Map.Entry<String, Integer> entry : getTopPages()) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" +
                    unique + " unique)");

            rank++;
        }


        System.out.println("\nTraffic Sources:");

        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);

            double percent = (count * 100.0) / total;

            System.out.printf("%s: %.1f%%\n", source, percent);
        }
    }


    public static void main(String[] args) throws InterruptedException {

        RealTime dashboard = new RealTime();
        dashboard.processEvent("/article/breaking-news", "user_123", "google");
        dashboard.processEvent("/article/breaking-news", "user_456", "facebook");
        dashboard.processEvent("/sports/championship", "user_222", "direct");
        dashboard.processEvent("/sports/championship", "user_333", "google");
        dashboard.processEvent("/sports/championship", "user_222", "google");
        dashboard.processEvent("/tech/ai-future", "user_888", "google");
        dashboard.getDashboard();
    }
}