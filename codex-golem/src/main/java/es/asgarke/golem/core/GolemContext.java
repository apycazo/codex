package es.asgarke.golem.core;

import es.asgarke.golem.http.GolemServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class GolemContext {

  private final Class<?>[] classes;
  @Getter
  private final BeanFactory factory;
  @Getter
  private final String id;

  public static GolemContext startContext(Class<?> ... configClasses) {
    return new GolemContext(configClasses).initialize();
  }

  public GolemContext(Class<?> ... configClasses) {
    if (configClasses == null || configClasses.length == 0) {
      String msg = "Error: No initial config classes";
      log.error(msg);
      throw new RuntimeException(msg);
    } else {
      factory = new BeanFactory(this);
      id = UUID.randomUUID().toString();
      classes = configClasses;
      for (Class<?> clazz : configClasses) {
        log.info("Initial config class: {}", clazz);
      }
    }
  }

  public synchronized GolemContext initialize() {
    factory.initialize(classes);
    return this;
  }

  public GolemContext reportTo(Consumer<String> report) {
    if (report != null) {
      report.accept(stringReport());
    }
    return this;
  }

  public String stringReport() {
    return stringReport("\n");
  }

  public String stringReport(String delimiter) {
    return String.join(delimiter, factory.report());
  }

  public GolemServer startServer() {
    return new GolemServer(this).start();
  }

  public GolemServer startServer(int port) {
    return new GolemServer(this, port).start();
  }
}
