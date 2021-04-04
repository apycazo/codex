package apycazo.codex.auth;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

@Slf4j
@Configuration
@ComponentScan
@PropertySource("auth-app.properties")
public class AuthApp extends ResourceConfig {

  public AuthApp() {
    String packageName = AuthApp.class.getPackageName();
    log.info("Registering Jersey package: {}", packageName);
    packages(packageName);
  }

  public static void main(String[] args) {
    SimpleCommandLinePropertySource props = new SimpleCommandLinePropertySource(args);
    int port = Integer.parseInt(Optional.ofNullable(props.getProperty("port")).orElse("8080"));
    Server server = configureServer(port);
    try {
      log.info("Starting server at port {}", port);
      server.start();
      server.join();
    } catch (Exception e) {
      log.info("Error thrown: {}", e.getMessage(), e);
    }
  }

  public static Server configureServer(int port) {
    Server server = new Server(port);
    // --- rest api requires sessions (requires jersey-servlet library)
    ServletContextHandler servletContextHandler = new ServletContextHandler(SESSIONS);
    // --- configure context path and context handler
    servletContextHandler.setContextPath("/");
    server.setHandler(servletContextHandler);
    // --- configure resource
    ServletHolder servletHolder = new ServletHolder(new ServletContainer(new AuthApp()));
    servletHolder.setInitOrder(0);
    servletContextHandler.addServlet(servletHolder, "/*");
    // --- add spring support
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.registerShutdownHook();
    // --- Initial spring scan package
    context.scan(AuthApp.class.getPackageName());
    servletContextHandler.addEventListener(new ContextLoaderListener(context));
    // --- done
    return server;
  }

  @Path("")
  @Component
  public static class InfoEndpoint {

    private final Map<String, Object> map;

    public InfoEndpoint() {
      map = new HashMap<>();
      map.put("app", "codex-auth");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> info() {
      map.put("ts", Instant.now().toString());
      return map;
    }
  }
}
