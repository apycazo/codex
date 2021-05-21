package es.asgarke.golem.http;

import com.sun.net.httpserver.HttpServer;
import es.asgarke.golem.core.BeanFactory;
import es.asgarke.golem.core.BeanProperties;
import es.asgarke.golem.core.GolemContext;
import es.asgarke.golem.core.constructors.BeanDefinition;
import es.asgarke.golem.http.definitions.MediaType;
import es.asgarke.golem.http.types.BinaryMediaTypeMapper;
import es.asgarke.golem.http.types.JsonMediaTypeMapper;
import es.asgarke.golem.http.types.MediaTypeMapper;
import es.asgarke.golem.http.types.PlainTextMediaTypeMapper;
import es.asgarke.golem.tools.ParserTool;
import es.asgarke.golem.tools.StringOps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static es.asgarke.golem.http.definitions.ServerConfigConstants.*;

@Slf4j
public class GolemServer {

  private final HttpServer server;
  private final String basePath;
  @Getter
  private final GolemContext golemContext;

  public static GolemServer startServer(Class<?>... classes) {
    return startServer(-1, classes);
  }

  public static GolemServer startServer(int httpPort, Class<?>... classes) {
    Instant start = Instant.now();
    GolemContext golemContext = GolemContext.startContext(classes);
    GolemServer golemServer = new GolemServer(golemContext, httpPort).start();
    Instant finish = Instant.now();
    log.info("Start time was {} ms", Duration.between(start, finish).toMillis());
    return golemServer;
  }

  public GolemServer (GolemContext context) {
    this(context, -1);
  }

  public GolemServer (GolemContext context, int portOverride) {
    this.golemContext = context;
    BeanFactory factory = golemContext.getFactory();
    // load default properties
    BeanProperties properties = factory.getProperties();
    int port = ParserTool
      .readInt(properties.resolvePropertyTemplate(PROPERTY_PORT))
      .orElseThrow(() -> new RuntimeException("Invalid port property value"));
    if (portOverride >= 0) {
      port = portOverride;
    }
    int poolSize = ParserTool
      .readInt(properties.resolvePropertyTemplate(PROPERTY_POOL))
      .orElseThrow(() -> new RuntimeException("Invalid pool size property value"));
    basePath = properties.resolvePropertyTemplate(PROPERTY_PATH);
    // register media type handlers if they are not present yet
    registerMediaTypeMappers(factory);
    // create the server
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      port = server.getAddress().getPort();
      log.info("Server bound to port {}", port);
    } catch (IOException e) {
      log.error("Unable to bind server address", e);
      throw new RuntimeException("Invalid http address: " + port);
    }
    // create the thread pool
    server.setExecutor(poolSize > 0 ? Executors.newFixedThreadPool(poolSize) : null);
    // register endpoint handler
    List<BeanDefinition<?>> restResources = factory.registerRestResources();
    StaticResolver staticResolver = initializeStaticResolver(properties);
    RequestManager manager = new RequestManager(factory, basePath, restResources, staticResolver);
    server.createContext(basePath, manager);
    // create a shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        log.info("Stopping server...");
        server.stop(5);
        log.info("Server stopped");
      } catch (Exception e) {
        log.warn("Server stop failed", e);
      }
    }));
  }

  public GolemServer start() {
    try {
      log.info("Starting up server...");
      server.start();
      log.info("Server listening at port '{}', with base path '{}'", getHttpPort(), basePath);
    } catch (Exception e) {
      log.warn("Unable to start server (¿already running?): {}", e.getMessage());
    }
    return this;
  }

  public HttpServer getServer() {
    return server;
  }

  public int getHttpPort() {
    return server.getAddress().getPort();
  }

  public void stop() {
    server.stop(0);
  }

  private void registerMediaTypeMappers(BeanFactory factory) {
    Set<MediaTypeMapper> mappers = factory
      .findBeansMatching(MediaTypeMapper.class)
      .map(BeanDefinition::getClazz)
      .map(MediaTypeMapper.class::cast)
      .collect(Collectors.toSet());
    if (mappers.stream().noneMatch(v -> v.canMapMediaType(MediaType.APPLICATION_JSON))) {
      factory.registerSingleton(JsonMediaTypeMapper.class);
    }
    if (mappers.stream().noneMatch(v -> v.canMapMediaType(MediaType.TEXT_PLAIN))) {
      factory.registerSingleton(PlainTextMediaTypeMapper.class);
    }
    if (mappers.stream().noneMatch(v -> v.canMapMediaType(MediaType.IMAGE_ANY))) {
      factory.registerSingleton(BinaryMediaTypeMapper.class);
    }
  }

  private StaticResolver initializeStaticResolver(BeanProperties properties) {
    StaticResolver resolver = new StaticResolver();
    String classpathValues = properties.resolvePropertyTemplate(PROPERTY_STATIC_CP);
    if (StringOps.isNotEmpty(classpathValues)) {
      String[] paths = StringOps.splitCommaAndTrim(classpathValues);
      resolver.addClassPathLocations(paths);
    }
    String fileSystemValues = properties.resolvePropertyTemplate(PROPERTY_STATIC_FS);
    if (StringOps.isNotEmpty(fileSystemValues)) {
      String[] paths = StringOps.splitCommaAndTrim(fileSystemValues);
      resolver.addFilePathLocations(paths);
    }
    return resolver;
  }
}
