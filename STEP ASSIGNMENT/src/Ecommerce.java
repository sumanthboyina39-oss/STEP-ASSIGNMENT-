
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Ecommerce {
    private final ConcurrentHashMap<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> waitingListMap = new ConcurrentHashMap<>();
    public void addProduct(String productId, int initialStock) {
        stockMap.put(productId, new AtomicInteger(initialStock));
        waitingListMap.put(productId, new ConcurrentLinkedQueue<>());
    }
    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        return stock != null ? stock.get() : 0;
    }

    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) {
            return "Product not found";
        }

        int currentStock;
        do {
            currentStock = stock.get();
            if (currentStock == 0) {
                waitingListMap.get(productId).add(userId);
                return "Added to waiting list, position #" + waitingListMap.get(productId).size();
            }
        } while (!stock.compareAndSet(currentStock, currentStock - 1));

        return "Success, " + (currentStock - 1) + " units remaining";
    }

    // Get waiting list size
    public int getWaitingListSize(String productId) {
        ConcurrentLinkedQueue<Integer> queue = waitingListMap.get(productId);
        return queue != null ? queue.size() : 0;
    }

    // Example simulation
    public static void main(String[] args) throws InterruptedException {
        Ecommerce manager = new Ecommerce();
        manager.addProduct("IPHONE15_256GB", 100);

        ExecutorService executor = Executors.newFixedThreadPool(50);
        for (int i = 1; i <= 105; i++) {
            final int userId = i;
            executor.submit(() -> {
                String result = manager.purchaseItem("IPHONE15_256GB", userId);
                System.out.println("User " + userId + ": " + result);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Final stock: " + manager.checkStock("IPHONE15_256GB"));
        System.out.println("Waiting list size: " + manager.getWaitingListSize("IPHONE15_256GB"));
    }
}
