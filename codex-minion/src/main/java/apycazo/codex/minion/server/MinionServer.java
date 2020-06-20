package apycazo.codex.minion.server;

import apycazo.codex.minion.common.CoreException;
import apycazo.codex.minion.context.BeanRecord;
import apycazo.codex.minion.context.MinionContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static apycazo.codex.minion.common.StatusCode.INVALID_ADDRESS;
import static apycazo.codex.minion.common.StatusCode.INVALID_MAPPING;

// ref: https://dzone.com/articles/simple-http-server-in-java
@Slf4j
public class MinionServer {

  public static final String PROP_POOL_SIZE = "minion.server.pool";
  public static final String PROP_HTTP_PORT = "minion.server.http.port";
  public static final String BEAN_MAPPER_NAME = "minion-server-mapper";

  private final MinionContext context;
  private ObjectMapper mapper;
  private int poolSize;
  private int port;
  private String basePath;
  @Getter
  private HttpServer server;

  public MinionServer(MinionContext context) {
    this.context = context;
    this.poolSize = 10;
    this.basePath = "";
    this.port = 8080;
  }

  public MinionServer start() {
    // start the bean context first
    context.start();
    // look for configuration properties (they override default values)
    port = Integer.parseInt(context.getProperties().getProperty(PROP_HTTP_PORT, String.valueOf(port)));
    poolSize = Integer.parseInt(context.getProperties().getProperty(PROP_POOL_SIZE, String.valueOf(poolSize)));
    // resolve the object mapper to use when serializing
    resolveObjectMapper();
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      log.info("Server bound to port {}", port);
    } catch (IOException e) {
      log.error("Unable to bind server address", e);
      throw new CoreException(INVALID_ADDRESS);
    }
    ThreadPoolExecutor threadPoolExecutor = poolSize > 0
      ? (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize)
      : null;
    server.setExecutor(threadPoolExecutor);
    List<BeanRecord> endpointRecords = context.getCatalog().fetchCandidates(Endpoint.class);
    if (endpointRecords.isEmpty()) {
      log.warn("No endpoints found to serve");
    } else {
      for (BeanRecord endpointRecord : endpointRecords) {
        Endpoint endpoint = endpointRecord.getInstanceAs(Endpoint.class);
        String path = mapping(endpoint);
        RequestHandler handler = new RequestHandler(endpoint, mapper);
        server.createContext(path, handler);
        log.info("Mapped {} to {}", endpoint.getClass().getName(), endpoint.path());
      }
    }
    server.start();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        // stop the server, waiting up to 5 seconds for exchanges to be processed
        if (server != null) server.stop(5);
      } catch (Exception e) {
        log.warn("Server stop failed", e);
      }
    }));
    log.info("Server started at '{}'", server.getAddress().toString());
    return this;
  }

  public MinionServer basePath(String basePath) {
    this.basePath = basePath;
    return this;
  }

  public String basePath() {
    return basePath;
  }

  public MinionServer poolSize(int poolSize) {
    this.poolSize = poolSize;
    return this;
  }

  public int port() {
    return port;
  }

  public MinionServer port(int port) {
    this.port = port;
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

  private void resolveObjectMapper() {
    List<BeanRecord> beanRecords = context.getCatalog().fetchCandidates(ObjectMapper.class, BEAN_MAPPER_NAME);
    if (beanRecords.isEmpty()) {
      mapper = new ObjectMapper();
      context.getCatalog().register(mapper, BEAN_MAPPER_NAME);
      log.info("Registered default mapper bean as '{}'", BEAN_MAPPER_NAME);
    } else {
      mapper = beanRecords.get(0).getInstanceAs(ObjectMapper.class);
      log.info("Using existing object mapper bean");
    }
  }
}
