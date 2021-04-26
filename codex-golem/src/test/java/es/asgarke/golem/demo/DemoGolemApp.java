package es.asgarke.golem.demo;

import es.asgarke.golem.core.annotations.Configuration;
import es.asgarke.golem.http.GolemServer;

/**
 * Starts a Golem web application, using this package as the base for the component scan
 */
@Configuration(propertySources = "classpath:golem.properties")
public class DemoGolemApp {

  public static void main(String[] args) {
    GolemServer.startServer(DemoGolemApp.class);
  }
}
