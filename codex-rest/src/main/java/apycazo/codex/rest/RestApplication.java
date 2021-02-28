package apycazo.codex.rest;

import apycazo.codex.rest.common.filter.CrossDomainFilter;
import apycazo.codex.rest.common.security.SecurityFilter;
import apycazo.codex.rest.common.server.ApplicationSettings;
import apycazo.codex.rest.common.server.JettyConfig;
import apycazo.codex.rest.common.server.SpringConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.RequestContextFilter;

@Slf4j
public class RestApplication {

  private static final String baseScanPath = RestApplication.class.getPackageName();
  private final Server server;

  /**
   * When used as a standalone application, initializes & starts the server.
   * @param args the arguments to process (no argument is required right now).
   * @throws Exception on init server failures.
   */
  public static void main(String[] args) throws Exception {
    initServer().start();
  }

  /**
   * Creates a new server and returns the configured instance (without starting it).
   * @return the initialized server instance.
   */
  public static Server initServer() {
    return new RestApplication().getServer();
  }

  /**
   * Creates a new server instance, including the spring context, and taking application settings from the
   * initialized spring context (bean: ApplicationSettings). Creates the resource configuration using the
   * settings read from the bean, which in turns uses the spring-parsed property files.
   */
  public RestApplication() {
    WebApplicationContext springContext = createSpringContext();
    ApplicationSettings appSettings = springContext.getBean(ApplicationSettings.class);
    ResourceConfig resourceConfig = createResourceConfig(appSettings);
    server = new JettyConfig(resourceConfig, springContext, appSettings).getServer();
  }

  private ResourceConfig createResourceConfig(ApplicationSettings appSettings) {
    ResourceConfig resourceConfig = new ResourceConfig();
    resourceConfig.packages(baseScanPath);
    // binds http requests to service threads
    resourceConfig.register(RequestContextFilter.class);
    // jersey implementation of resource context
    resourceConfig.register(JerseyResourceContext.class);
    // security filter
    if (appSettings.isSecurityEnabled()) {
      resourceConfig.register(SecurityFilter.class);
    } else {
      log.warn("Security filter is disabled");
    }
    // cross-domain filter
    if (appSettings.isCorsEnabled()) {
      resourceConfig.register(CrossDomainFilter.class);
      log.info("Cross Domain filter enabled");
    }
    return resourceConfig;
  }

  public WebApplicationContext createSpringContext() {
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.registerShutdownHook();
    context.scan(baseScanPath);
    context.register(SpringConfig.class);
    context.refresh(); // initialize the context now, we will need to take the settings bean.
    return context;
  }

  /**
   * Returns the configured Jetty server instance (if any).
   * @return the server instance.
   */
  public Server getServer() {
    return server;
  }

}
