package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class Threading {

  public static void main(String[] args) throws Exception {
    scheduled_task();
    executor_service();
  }

  private static void scheduled_task() throws InterruptedException {
    log.info("Running scheduled task");
    ScheduledExecutorService  executor = Executors.newSingleThreadScheduledExecutor();
    executor.schedule(() -> log.info("Done"), 2, TimeUnit.SECONDS);
    if (executor.awaitTermination(3, TimeUnit.SECONDS)) {
      log.info("Completed");
    }
    executor.shutdownNow();
    log.info("Scheduled task complete");
  }

  private static void executor_service() throws InterruptedException, ExecutionException {
    int poolSize = 3;
    ExecutorService executor = Executors.newFixedThreadPool(poolSize);
    List<Sleepy> taskList = Arrays.asList(
      new Sleepy(1),
      new Sleepy(2),
      new Sleepy(3),
      new Sleepy(4),
      new Sleepy(5)
    );

    Instant start = Instant.now();
    log.info("Running tasks");
    List<Future<String>> futures = executor.invokeAll(taskList);
    int i = 0;
    // blocking call: will only print results when all tasks are complete
    for (Future<String> future : futures) {
      log.info("{}: {}", i++, future.get());
    }
    Duration duration = Duration.between(start, Instant.now());
    log.info("Task duration: {}", duration);
    executor.shutdownNow();
  }

  private static class Sleepy implements Callable<String> {

    private final int id;

    public Sleepy(int id) {
      this.id = id;
    }

    @Override
    public String call() {
      try {
        TimeUnit.SECONDS.sleep(3);
        log.info("{} completed", id); // shows result as soon as the task completes
        return "Success (" + id + ")";
      } catch (Exception e) {
        System.out.println("Error thrown: " + e.getMessage());
        e.printStackTrace(System.err);
        return "Error (" + id + ")";
      }
    }
  }
}
