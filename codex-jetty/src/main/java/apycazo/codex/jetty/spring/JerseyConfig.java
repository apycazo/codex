package apycazo.codex.jetty.spring;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ResourceConfig;

@Slf4j
public class JerseyConfig extends ResourceConfig {

  public JerseyConfig() {
    String packageName = JerseyConfig.class.getPackageName();
    log.info("Registering Jersey package: {}", packageName);
    packages(packageName);
  }
}
