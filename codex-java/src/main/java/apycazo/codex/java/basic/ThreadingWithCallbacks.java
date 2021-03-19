package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Slf4j
public class ThreadingWithCallbacks {

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    log.info("Starting test");
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    List<RunnableTask> tasks = new ArrayList<>();
    Consumer<Integer> callback = (i) -> log.info("{} completed", i);
    IntStream.range(0, 6).forEach(v -> tasks.add((new RunnableTask(v, callback))));
    List<Future<Void>> futures = executorService.invokeAll(tasks);
    // we need to take all futures to mark the executor as completed.
    for (Future<Void> future : futures) future.get();
    executorService.shutdownNow();
  }

  private static class RunnableTask implements Callable<Void> {

    private final Consumer<Integer> callback;
    private final int id;

    public RunnableTask(int id, Consumer<Integer> callback) {
      this.id = id;
      this.callback = callback;
    }

    @Override
    public Void call() {
      try {
        TimeUnit.SECONDS.sleep(3);
        Optional.ofNullable(callback).ifPresent(v -> v.accept(id));
      } catch (Exception e) {
        System.out.println("Error thrown: " + e.getMessage());
        e.printStackTrace(System.err);
      }
      return null;
    }
  }
}
