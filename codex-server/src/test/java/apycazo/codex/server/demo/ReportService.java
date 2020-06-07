package apycazo.codex.server.demo;

import apycazo.codex.server.AppContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

// tests bean post-construct, field injection
@Slf4j
@Singleton
@Named("encoder-service-bean")
public class ReportService {

  @Inject // will create a singleton a report task
  private ReportTask reportTask;
  @Inject
  private AppContext appContext;

  private final Timer timer = new Timer();

  @PostConstruct
  public void start() {
    appContext.singletonList().forEach(name -> log.info("Registered singleton '{}'", name));
    long delay = 1_000L;
    long period = 2_500L;
    timer.scheduleAtFixedRate(reportTask, delay, period);
  }

  public void stop() {
    timer.cancel();
  }

  public List<String> getResults() {
    return reportTask.getResults().collect(Collectors.toList());
  }

}
