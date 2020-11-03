package apycazo.codex.jetty.basic;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.PathResource;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Starts a Jetty server publishing the content of 'user.dir', which should target this
 * repository path.
 */
public class FileServerDemo {

  public static void main(String[] args) {
    int port = 8080;
    Path userDir = Paths.get(System.getProperty("user.dir"));
    PathResource pathResource = new PathResource(userDir);
    Server server = new Server(port);
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{"index.html"});
    resourceHandler.setBaseResource(pathResource);
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
    server.setHandler(handlers);
    try {
      server.start();
      server.join();
    } catch (Exception e) {
      System.err.println("Failed to init server: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }
}
