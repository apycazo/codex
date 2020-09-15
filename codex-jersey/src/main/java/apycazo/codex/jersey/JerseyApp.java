package apycazo.codex.jersey;

import apycazo.codex.jersey.config.AppResourceConfig;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class JerseyApp {

  public static void main(String[] args) {
    start(8080);
  }

  static HttpServer start(int port) {
    URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();
    ResourceConfig config = new AppResourceConfig();
    HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(2)));
    return server;
  }
}
