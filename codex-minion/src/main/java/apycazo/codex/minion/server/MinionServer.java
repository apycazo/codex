package apycazo.codex.minion.server;

import apycazo.codex.minion.common.CoreException;
import apycazo.codex.minion.context.MinionContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static apycazo.codex.minion.common.StatusCode.INVALID_ADDRESS;
import static apycazo.codex.minion.common.StatusCode.INVALID_MAPPING;

// ref: https://dzone.com/articles/simple-http-server-in-java
@Slf4j
public class MinionServer {

  private final MinionContext context;
  private final List<Endpoint> endpoints;
  private final ObjectMapper mapper;
  private int poolSize = 10;
  private int port = 8008;
  private String basePath = "";
  private HttpServer server;

  public MinionServer(MinionContext context) {
    this.context = context;
    endpoints = new ArrayList<>();
    mapper = new ObjectMapper();
    port = 8080;
  }

  public MinionServer start() {
    // start the bean context first
    context.start();
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
    } catch (IOException e) {
      log.error("Unable to bind server address", e);
      throw new CoreException(INVALID_ADDRESS);
    }
    ThreadPoolExecutor threadPoolExecutor = poolSize > 0
      ? (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize)
      : null;
    server.setExecutor(threadPoolExecutor);
    if (endpoints.isEmpty()) {
      log.warn("No endpoints found to serve");
    } else {
      for (Endpoint endpoint : endpoints) {
        String path = mapping(endpoint);
        RequestHandler handler = new RequestHandler(endpoint, mapper);
        server.createContext(path, handler);
        log.info("Mapped {} to {}", endpoint.getClass().getName(), endpoint.path());
      }
    }
    server.start();
    log.info("Server started at '{}'", server.getAddress().toString());
    return this;
  }

  private String mapping(Endpoint endpoint) {
    if (endpoint.path() == null) {
      throw new CoreException(INVALID_MAPPING);
    } else if (endpoint.path().startsWith("/")) {
      return basePath + endpoint.path();
    } else {
      return basePath + "/" + endpoint.path();
    }
  }

}
