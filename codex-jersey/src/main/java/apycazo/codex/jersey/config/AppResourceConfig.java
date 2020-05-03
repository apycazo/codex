package apycazo.codex.jersey.config;

import apycazo.codex.jersey.JerseyApp;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class AppResourceConfig extends ResourceConfig {

  public AppResourceConfig() {
    log.info("Configuring the app");
    AnnotationConfigApplicationContext springContext;
    springContext = new AnnotationConfigApplicationContext();
    String scanPath = JerseyApp.class.getPackage().getName();
    springContext.scan(scanPath);
    springContext.refresh();
    property("contextConfig", springContext);
    packages(JerseyApp.class.getPackage().getName());
  }
}
