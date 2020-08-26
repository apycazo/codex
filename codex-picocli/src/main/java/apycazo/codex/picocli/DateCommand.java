package apycazo.codex.picocli;

import picocli.CommandLine;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@CommandLine.Command(name = "date", description = "Prints current date")
public class DateCommand implements Runnable {

  @CommandLine.Option(names = {"-z", "--zone"}, required = false, description = "Time zone", defaultValue = "UTC")
  private String zoneId;

  @Override
  public void run() {
    String value = Instant.now().atZone(ZoneId.of(zoneId)).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    System.out.println(value);
  }
}
