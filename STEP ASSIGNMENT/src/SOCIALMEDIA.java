import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 class SocialMedia {
    private Map<String, Integer> userMap = new HashMap<>();
    private Map<String, Integer> attemptMap = new HashMap<>();

    public void registerUser(String username, int userId) {
        userMap.put(username, userId);
    }

    public boolean checkAvailability(String username) {
        recordAttempt(username);
        return !userMap.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        String alt1 = username + "1";
        if (!userMap.containsKey(alt1)) {
            suggestions.add(alt1);
        }

        String alt2 = username + "2";
        if (!userMap.containsKey(alt2)) {
            suggestions.add(alt2);
        }

        String alt3 = username + "3";
        if (!userMap.containsKey(alt3)) {
            suggestions.add(alt3);
        }

        String replaced = username.replace("_", ".");
        if (!userMap.containsKey(replaced)) {
            suggestions.add(replaced);
        }

        return suggestions;
    }

    private void recordAttempt(String username) {
        attemptMap.put(username, attemptMap.getOrDefault(username, 0) + 1);
    }

    public String getMostAttempted() {
        return attemptMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static void main(String[] args)
    {
        SocialMedia checker =new SocialMedia();
        checker.registerUser("john_doe", 12345);
        checker.registerUser("jane_smith", 67890);

        System.out.println("john_doe available? " + checker.checkAvailability("john_doe"));
        System.out.println("jane_smith available? " + checker.checkAvailability("jane_smith"));
        System.out.println("new_user available? " + checker.checkAvailability("new_user"));

        System.out.println("Suggestions for john_doe: " + checker.suggestAlternatives("john_doe"));

        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");

        System.out.println("Most attempted username: " + checker.getMostAttempted());
    }
}


