package apycazo.codex.java.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@EnableScheduling
@PropertySource("classpath:application.properties")
public class SpringScheduler implements SchedulingConfigurer {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringScheduler.class);
    ctx.refresh();
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegister) {
    taskRegister.setScheduler(taskExecutor());
  }

  @Bean(destroyMethod="shutdown")
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(100);
  }

  @Component
  public static class Task {

    private AtomicInteger count = new AtomicInteger(0);

    @Scheduled(cron = "${cli.cron}")
    public void scheduledAction () {
      log.info("Scheduler action run: {}", count.incrementAndGet());
    }
  }
}
