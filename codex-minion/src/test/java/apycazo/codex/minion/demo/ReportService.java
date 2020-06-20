package apycazo.codex.minion.demo;

import apycazo.codex.minion.context.MinionContext;
import apycazo.codex.minion.context.PropertyValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@Named("report-service-bean")
public class ReportService {

  @Inject // will create a singleton a report task
  private ReportTask reportTask;
  @Inject
  private MinionContext context;
  @Inject
  private ObjectMapper mapper;
  @PropertyValue("application.name")
  private String applicationName;
  @PropertyValue("application.intValue")
  private int intValue;
  @PropertyValue("application.booleanValue")
  private boolean booleanValue;

  private final Timer timer = new Timer();

  @PostConstruct
  public void start() {
    log.info("Starting application '{}'", Optional.ofNullable(applicationName).orElse("default"));
    log.info("Bool value: {}, Int value: {}", booleanValue, intValue);
    log.info("Mapper available? {}", mapper != null);
    context.getCatalog().records().forEach(record -> log.info("Registered {}", record));
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
