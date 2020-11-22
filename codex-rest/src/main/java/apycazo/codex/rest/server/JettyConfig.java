package apycazo.codex.rest.server;

import apycazo.codex.rest.common.ServletGatewayFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

@Slf4j
@RequiredArgsConstructor
public class JettyConfig {

  private final ResourceConfig resourceConfig;
  private final WebApplicationContext springContext;
  private final ApplicationSettings appSettings;

  public Server getServer() {
    Server server = new Server();
    configureServerPorts(server);
    // --- create the handler list (note: the order is relevant).
    HandlerList handlers = new HandlerList();
    handlers.addHandler(configureJerseyHandler());
    handlers.addHandler(configureStaticHandler());
    handlers.addHandler(new DefaultHandler());
    server.setHandler(handlers);
    return server;
  }

  private void configureServerPorts(Server server) {
    HttpConfiguration httpConfig = new HttpConfiguration();
    HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfig);
    // first connector
    ServerConnector http = new ServerConnector(server, httpConnectionFactory);
    http.setPort(appSettings.getServerHttpPort());
    server.setConnectors(new Connector[]{http});
  }

  private Handler configureStaticHandler() {
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(false);
    // create a context for the resource and set the base content path
    ContextHandler contextHandler = new ContextHandler();
    contextHandler.setContextPath(appSettings.getStaticMapping());
    contextHandler.setBaseResource(Resource.newClassPathResource("public"));
    contextHandler.setHandler(resourceHandler);
    return contextHandler;
  }

  private Handler configureJerseyHandler() {
    final int options = appSettings.isStateless() ? NO_SESSIONS : SESSIONS;
    ServletContextHandler servletContextHandler = new ServletContextHandler(options);
    // --- configure all jetty-related properties into the servlet context handler
    appSettings.getJettyProperties().forEach(servletContextHandler::setAttribute);
    // --- configure context path and context handler
    String contextPath = appSettings.getJerseyMapping();
    log.info("Registered context path: {}", contextPath);
    servletContextHandler.setContextPath(contextPath);
    ServletHolder servletHolder = new ServletHolder(new ServletContainer(resourceConfig));
    servletHolder.setInitOrder(0);
    servletContextHandler.addServlet(servletHolder, "/*");
    // --- add spring support
    servletContextHandler.addEventListener(new ContextLoaderListener(springContext));
    // --- add servlet gateway filter
    servletContextHandler.addFilter(ServletGatewayFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    return servletContextHandler;
  }
}
