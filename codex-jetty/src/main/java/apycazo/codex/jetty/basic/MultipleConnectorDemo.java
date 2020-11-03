package apycazo.codex.jetty.basic;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Creates two different ports to listen on.
 * https://www.eclipse.org/jetty/documentation/current/embedded-examples.html
 */
public class MultipleConnectorDemo {

  public static void main(String[] args) {
    // do not specify the port, since we are implementing two options.
    Server server = new Server();
    // common config
    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setOutputBufferSize(32768);
    HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfig);
    // first connector
    ServerConnector http1 = new ServerConnector(server, httpConnectionFactory);
    http1.setPort(8081);
    // second connector
    ServerConnector http2 = new ServerConnector(server, httpConnectionFactory);
    http2.setPort(8082);
    // set the connectors
    server.setConnectors(new Connector[]{http1, http2});
    // demo handler
    server.setHandler(new DemoRequestHandler());
    try {
      server.start();
      server.join();
    } catch (Exception e) {
      System.err.println("Failed to init server: " + e.getMessage());
      e.printStackTrace(System.err);
    }
  }

  private static class DemoRequestHandler extends AbstractHandler {

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
      response.setContentType("text/html;charset=utf-8");
      response.setStatus(HttpServletResponse.SC_OK);
      baseRequest.setHandled(true);
      response.getWriter().printf("<h1>Request from port %d ok</h1>", request.getLocalPort());
    }
  }
}
