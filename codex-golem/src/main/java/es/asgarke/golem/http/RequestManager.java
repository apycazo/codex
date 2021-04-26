package es.asgarke.golem.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import es.asgarke.golem.core.BeanFactory;
import es.asgarke.golem.core.constructors.BeanDefinition;
import es.asgarke.golem.http.annotations.Endpoint;
import es.asgarke.golem.http.annotations.RestResource;
import es.asgarke.golem.http.definitions.HeaderKeys;
import es.asgarke.golem.http.definitions.HttpMethod;
import es.asgarke.golem.http.definitions.MediaType;
import es.asgarke.golem.http.types.ExceptionMapper;
import es.asgarke.golem.http.types.MediaTypeMapper;
import es.asgarke.golem.http.types.Response;
import es.asgarke.golem.tools.StringTool;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles request mapping, transforming http exchanges into bean method calls and generates responses.
 * Maps all endpoints by their mapping segments, and traverses the nodes trying to find a match.
 * <br>
 * Example: /api/v2/:id/ will have 3 nodes to traverse: api, v2, and id
 */
@Slf4j
public class RequestManager implements HttpHandler {

  private final MappingNode root;
  private final List<MediaTypeMapper> mediaTypeMappers;
  @SuppressWarnings("rawtypes")
  private final List<ExceptionMapper> exceptionMappers;

  public RequestManager(
    BeanFactory factory, String rootPath, List<BeanDefinition<?>> endpointBeans) {
    this.root = new MappingNode();
    // register media mappers found
    this.mediaTypeMappers = factory.findBeansMatching(MediaTypeMapper.class)
      .map(definition -> definition.instance(factory))
      .map(MediaTypeMapper.class::cast)
      .collect(Collectors.toList());
    this.mediaTypeMappers.forEach(value -> log.info("Registered media mapper class {}", value.getClass()));
    // register exception mappers
    this.exceptionMappers = factory.findBeansMatching(ExceptionMapper.class)
      .map(definition -> definition.instance(factory))
      .map(ExceptionMapper.class::cast)
      .collect(Collectors.toList());
    this.mediaTypeMappers.forEach(value -> log.info("Registered media mapper class {}", value.getClass()));
    // register definitions
    log.info("Found {} beans registering endpoints", endpointBeans.size());
    for (BeanDefinition<?> definition : endpointBeans) {
      Class<?> clazz = definition.getClazz();
      RestResource config = clazz.getAnnotation(RestResource.class);
      String basePath = StringTool.joinPaths(rootPath, config.path());
      List<Method> methods = Arrays.stream(clazz.getMethods())
        .filter(m -> m.isAnnotationPresent(Endpoint.class))
        .collect(Collectors.toList());
      for (Method method : methods) {
        Endpoint endpoint = method.getAnnotation(Endpoint.class);
        String endpointPath = StringTool.joinPaths(basePath, endpoint.path());
        String [] segments = endpointPath.substring(1).split("/");
        MappingNode terminal = root;
        for (String segment : segments) {
          terminal = terminal.traverseOrCreate(segment);
        }
        // create the actual handler here
        if (terminal == null) {
          throw new RuntimeException("Unable to resolve a terminal node");
        } else {
          String producedMedia = endpoint.produces().isBlank() ? config.produces() : endpoint.produces();
          String consumedMedia = endpoint.consumes().isBlank() ? config.consumes() : endpoint.consumes();
          Object instance = definition.instance(factory);
          if (instance == null) {
            String msg = "Invalid mapping: instance with definition " + definition.getClazz().getName() + " is null";
            log.error(msg);
            throw new RuntimeException(msg);
          } else {
            MediaTypeMapper mapper = resolveMapperForMediaType(consumedMedia);
            RequestProcessor processor = new RequestProcessor(consumedMedia, producedMedia, method, instance, mapper);
            terminal.setProcessor(endpoint.method(), processor);
            log.info("Added endpoint {} {} to {}::{}", endpoint.method(), endpointPath, clazz, method.getName());
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Response resolveRequest(HttpExchange exchange) {
    String path = exchange.getRequestURI().getPath();
    log.info("Resolving {} request for path {}", exchange.getRequestMethod(), path);
    String[] requestPathSegments = path.substring(1).split("/");
    MappingNode node = root;
    Map<String, String> pathVariables = new HashMap<>();
    int index = 0;
    while (node != null && index < requestPathSegments.length) {
      String currentSegment = requestPathSegments[index++];
      node = node.resolveNext(currentSegment);
      node.placeholderNames.forEach(variable -> pathVariables.put(variable, currentSegment));
    }
    if (node == null) {
      log.info("Node for found: the path '{}' is not mapped", path);
      return Response.notFound();
    } else {
      HttpMethod requestMethod = HttpMethod // extract the request method to resolve the processor.
        .parseFrom(exchange.getRequestMethod())
        .orElseThrow(() -> new RuntimeException("Invalid request method: " + exchange.getRequestMethod()));
      RequestProcessor requestProcessor = node.processors.get(requestMethod);
      if (requestProcessor == null) {
        return Response.notFound();
      } else {
        try {
          return requestProcessor.process(exchange, pathVariables);
        } catch (InvocationTargetException e) {
          Throwable actualException = e.getCause();
          if (actualException == null) {
            return Response.internalServerError(e);
          } else {
            return exceptionMappers.stream()
              .filter(mapper -> mapper.dealsWith(actualException))
              .findFirst()
              .map(mapper -> mapper.getResponse(actualException))
              .orElse(Response.requestError(actualException));
          }
        }
      }
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // create a request context from the exchange?
    Response response = resolveRequest(exchange);
    // resolve the media type
    String mediaType = response.getMediaType();
    if (mediaType != null && !mediaType.isBlank()) {
      exchange.getResponseHeaders().add(HeaderKeys.CONTENT_TYPE, mediaType);
    }
    // resolve a media mapper (defaults to text/plain)
    MediaTypeMapper mediaTypeMapper = resolveMapperForMediaType(mediaType);
    if (mediaTypeMapper == null) {
      log.warn("No media mapper resolved for {}", mediaType);
      throw new RuntimeException("Failed to find a valid media type mapper for " + mediaType);
    }
    byte[] bodyBytes = mediaTypeMapper.toByteArray(response.getContent());
    // actually send the response
    exchange.sendResponseHeaders(response.getStatus(), bodyBytes.length);
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write(bodyBytes);
    outputStream.flush();
    exchange.close();
  }

  private MediaTypeMapper resolveMapperForMediaType(String mediaType) {
    String effectiveMediaType = StringTool.isEmpty(mediaType) ? MediaType.TEXT_PLAIN : mediaType;
    return mediaTypeMappers.stream()
      .filter(mapper -> mapper.canMapMediaType(effectiveMediaType))
      .findFirst()
      .orElse(null);
  }

  private static class MappingNode {
    private final Map<String, MappingNode> leaves;
    private final Set<String> placeholderNames;
    private final Map<HttpMethod, RequestProcessor> processors;

    public MappingNode() {
      this.leaves = new HashMap<>();
      this.placeholderNames = new HashSet<>();
      this.processors = new HashMap<>();
    }

    public MappingNode traverseOrCreate(String name) {
      boolean isPathVariable = name.startsWith(":");
      String leafName = name.startsWith(":") ? "*" : name;
      MappingNode node = leaves.containsKey(leafName)
        ? leaves.get(leafName)
        : new MappingNode();
      if (isPathVariable) {
        node.placeholderNames.add(name.substring(1));
      }
      leaves.put(leafName, node);
      return node;
    }

    public MappingNode resolveNext(String name) {
      return leaves.getOrDefault(name, leaves.get("*"));
    }

    public void setProcessor(HttpMethod method, RequestProcessor processor) {
      if (processors.containsKey(method)) {
        throw new RuntimeException(String.format("Method %s already mapped for the current path", method.name()));
      } else {
        processors.put(method, processor);
      }
    }
  }
}
