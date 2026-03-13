import java.util.*;
import java.util.concurrent.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long timestamp;     // when entry was created
    long expiryTime;    // TTL in ms

    DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.timestamp = System.currentTimeMillis();
        this.expiryTime = this.timestamp + ttlSeconds * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class dncCache {
    private final int capacity;
    private final Map<String, DNSEntry> cache;
    private long hits = 0;
    private long misses = 0;

    public dncCache(int capacity) {
        this.capacity = capacity;
        // LinkedHashMap with access-order for LRU eviction
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > dncCache.this.capacity;
            }
        };

        // Background thread to clean expired entries
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            synchronized (cache) {
                Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, DNSEntry> entry = it.next();
                    if (entry.getValue().isExpired()) {
                        it.remove();
                    }
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    // Simulated upstream DNS query
    private String queryUpstream(String domain) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        // Fake IP for demo purposes
        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    public String resolve(String domain, long ttlSeconds) {
        synchronized (cache) {
            DNSEntry entry = cache.get(domain);
            if (entry != null && !entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                misses++;
                String ip = queryUpstream(domain);
                cache.put(domain, new DNSEntry(domain, ip, ttlSeconds));
                return (entry == null ? "Cache MISS" : "Cache EXPIRED") + " → " + ip + " (TTL: " + ttlSeconds + "s)";
            }
        }
    }

    public String getCacheStats() {
        long total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return String.format("Hit Rate: %.2f%%, Hits: %d, Misses: %d", hitRate, hits, misses);
    }

    // Demo
    public static void main(String[] args) throws InterruptedException {
        dncCache dncCache = new dncCache(5);

        System.out.println(dncCache.resolve("google.com", 3));
        System.out.println(dncCache.resolve("google.com", 3));
        Thread.sleep(4000); // wait for TTL expiry
        System.out.println(dncCache.resolve("google.com", 3));

        System.out.println(dncCache.resolve("microsoft.com", 5));
        System.out.println(dncCache.resolve("openai.com", 5));
        System.out.println(dncCache.resolve("github.com", 5));
        System.out.println(dncCache.resolve("stackoverflow.com", 5));
        System.out.println(dncCache.resolve("reddit.com", 5)); // triggers LRU eviction

        System.out.println(dncCache.getCacheStats());
    }
}

