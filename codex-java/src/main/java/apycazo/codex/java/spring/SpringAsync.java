package apycazo.codex.java.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@Slf4j
@Configuration
public class SpringAsync {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringAsync.class);
    ctx.refresh();
  }

  @Configuration
  @EnableAsync
  public static class AsyncConfig extends AsyncConfigurerSupport {

    @Override
    public Executor getAsyncExecutor() {
      log.info("Configuring async executor");
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(4);
      executor.setMaxPoolSize(8);
      executor.setQueueCapacity(100);
      executor.setThreadNamePrefix("async-");
      executor.initialize();
      return executor;
    }
  }

  @Service
  public static class AsyncProducer {

    /**
     * Async method to call. Must be public and not invoked from within the same class.
     * @param value the value to return.
     * @param delay the delay before returning.
     * @return the value provided.
     * @throws InterruptedException
     */
    @Async // removing this annotation increases time from 3 seconds to 8+
    public Future<String> getAsyncValue(String value, int delay) throws InterruptedException {
      if (delay > 0) Thread.sleep(delay);
      return new AsyncResult<>(value);
    }
  }

  @Service
  public static class AsyncConsumer {

    @Autowired
    private AsyncProducer producer;

    @PostConstruct
    private void validate () throws InterruptedException {
      log.info("Validating...");
      int delay = 2500; // 2.5 seconds to get the first result
      List<String> values = Arrays.asList("1","2","3");
      List<Future<String>> responses = new ArrayList<>(values.size());
      values.forEach(value -> {
        try {
          responses.add(producer.getAsyncValue(value, delay));
        } catch (InterruptedException e) {
          log.error("Error captured", e);
        }
      });
      // if the @Async annotation is removed, this logs nothing.
      while (responses.stream().anyMatch(response -> !response.isDone())) {
        log.info("Waiting a second, process has not finished yet");
        Thread.sleep(1000);
      }
      responses.forEach(response -> {
        try {
          log.info("Value: '{}'", response.get());
        } catch (InterruptedException | ExecutionException e) {
          log.error("Error captured", e);
        }
      });
      // service does not stop
      log.info("Done! service waits for more events, stop manually with ctrl+c");
    }
  }
}
