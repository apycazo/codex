package apycazo.codex.server.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public class ReportTask extends TimerTask {

  private final List<String> results = new ArrayList<>();

  @Override
  public void run() {
    String value = UUID.randomUUID().toString();
    log.info("Generated new value: {}", value);
    results.add(value);
  }

  public Stream<String> getResults() {
    return results.stream();
  }

}
