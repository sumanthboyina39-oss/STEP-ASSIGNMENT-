import java.util.*;

class Plagiarism{
    private Map<String, Set<String>> index = new HashMap<>();
    private Map<String, String> documents = new HashMap<>();
    private int N = 5; // n-gram size
    public void addDocument(String docId, String text) {
        documents.put(docId, text);
        List<String> ngrams = generateNGrams(text);

        for (String gram : ngrams) {
            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(docId);
        }
    }
    private List<String> generateNGrams(String text) {
        text = text.toLowerCase().replaceAll("[^a-zA-Z ]", "");
        String[] words = text.split("\\s+");

        List<String> grams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            grams.add(gram.toString().trim());
        }

        return grams;
    }
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNGrams(text);

        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {
            if (index.containsKey(gram)) {
                for (String doc : index.get(gram)) {
                    matchCount.put(doc, matchCount.getOrDefault(doc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + doc + "\"");

            System.out.printf("Similarity: %.2f%% ", similarity);

            if (similarity > 60)
                System.out.println("(PLAGIARISM DETECTED)");
            else if (similarity > 10)
                System.out.println("(Suspicious)");
            else
                System.out.println("(Low similarity)");
        }
    }

    public static void main(String[] args) {

        Plagiarism detector = new Plagiarism();

        // Database documents
        detector.addDocument("essay_089",
                "Artificial intelligence helps computers learn from data");

        detector.addDocument("essay_092",
                "Artificial intelligence helps computers learn from data and improve performance");

        // New submission
        detector.analyzeDocument("essay_123",
                "Artificial intelligence helps computers learn from data and improve results");
    }
}