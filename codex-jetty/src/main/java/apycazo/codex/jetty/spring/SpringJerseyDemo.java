package apycazo.codex.jetty.spring;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.Optional;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

@Slf4j
public class SpringJerseyDemo {

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
    // use this when JerseyConfig is not annotated with @Component
    // ServletHolder servletHolder = new ServletHolder(new ServletContainer(new JerseyConfig()));
    ServletHolder servletHolder = new ServletHolder(new ServletContainer());
    servletHolder.setInitOrder(0);
    servletContextHandler.addServlet(servletHolder, "/*");
    // --- add spring support
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.registerShutdownHook();
    // --- Initial spring scan package
    context.scan(SpringConfig.class.getPackageName());
    servletContextHandler.addEventListener(new ContextLoaderListener(context));
    // --- done
    return server;
  }

}
