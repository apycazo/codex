package apycazo.codex.jetty.basic;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

@Slf4j
public class JerseyDemo extends ResourceConfig {

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
    // --- rest api requires no session (requires jersey-servlet library)
    ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);
    // --- configure context path and context handler
    servletContextHandler.setContextPath("/");
    server.setHandler(servletContextHandler);
    // --- configure resource
    ServletHolder servletHolder = new ServletHolder(new ServletContainer(new JerseyDemo()));
    servletHolder.setInitOrder(0);
    servletContextHandler.addServlet(servletHolder, "/*");
    // --- done
    return server;
  }

  public JerseyDemo() {
    // avoiding using 'packages()' here since it would collide with other examples.
    register(JerseyController.class);
  }

  @Path("api")
  public static class JerseyController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> get() {
      Map<String, Object> map = new HashMap<>();
      map.put("controller.name", JerseyController.class.getName());
      map.put("timestamp", Instant.now().toEpochMilli());
      return map;
    }
  }

}
