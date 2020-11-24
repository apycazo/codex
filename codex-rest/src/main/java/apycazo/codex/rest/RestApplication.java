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

@Slf4j
public class RestApplication {

  private static final String baseScanPath = RestApplication.class.getPackageName();
  private final Server server;

  public static void main(String[] args) throws Exception {
    initServer().start();
  }

  public static Server initServer() {
    return new RestApplication().getServer();
  }

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

  public Server getServer() {
    return server;
  }

}
