package apycazo.codex.rest;

import apycazo.codex.rest.common.CrossDomainFilter;
import apycazo.codex.rest.security.SecurityFilter;
import apycazo.codex.rest.server.ApplicationSettings;
import apycazo.codex.rest.server.JettyConfig;
import apycazo.codex.rest.server.SpringConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.RequestContextFilter;

import java.util.function.Consumer;

@Slf4j
public class RestApplication {

  private static final String baseScanPath = RestApplication.class.getPackageName();

  public static void main(String[] args) throws Exception {
    new RestApplication().configureServer().start();
  }

  /**
   * Configures the application with the provided property locations exclusively
   * (this does not register the default locations).
   */
  public RestApplication() {
    springContext = createSpringContext();
    appSettings = springContext.getBean(ApplicationSettings.class);
    resourceConfig = createResourceConfig();
  }

  public ResourceConfig createResourceConfig() {
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

  public RestApplication configureResourceConfig(Consumer<ResourceConfig> config) {
    if (config != null) {
      config.accept(resourceConfig);
    }
    return this;
  }

  public RestApplication configureSpringContext(Consumer<WebApplicationContext> config) {
    if (config != null) {
      config.accept(springContext);
    }
    return this;
  }

  public RestApplication configureProperties(Consumer<ApplicationSettings> config) {
    if (config != null) {
      config.accept(appSettings);
    }
    return this;
  }

  public Server configureServer() {
    if (server == null) {
      server = new JettyConfig(resourceConfig, springContext, appSettings).getServer();
    }
    return server;
  }

  private final ResourceConfig resourceConfig;
  private final WebApplicationContext springContext;
  private final ApplicationSettings appSettings;
  private Server server;
}
