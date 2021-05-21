package es.asgarke.golem.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import es.asgarke.golem.common.OrderedComparator;
import es.asgarke.golem.core.BeanFactory;
import es.asgarke.golem.core.constructors.BeanDefinition;
import es.asgarke.golem.http.annotations.Endpoint;
import es.asgarke.golem.http.annotations.RestResource;
import es.asgarke.golem.http.definitions.*;
import es.asgarke.golem.http.types.ExceptionMapper;
import es.asgarke.golem.http.types.MediaTypeMapper;
import es.asgarke.golem.http.types.Response;
import es.asgarke.golem.tools.StringOps;
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
  private final StaticResolver staticResolver;
  private final List<RequestFilter> requestFilters;
  private final List<ResponseFilter> responseFilters;

  /**
   * Creates a request manager, which will map all rest resources and prepare the mapping nodes to resolve requests.
   * @param factory the factory to fetch beans.
   * @param rootPath the base path to apply to all endpoints.
   * @param endpointBeans the list of beans defining endpoints.
   * @param staticResolver when static content paths are present, the resolver to use.
   */
  public RequestManager(
    BeanFactory factory, String rootPath, List<BeanDefinition<?>> endpointBeans, StaticResolver staticResolver) {
    this.staticResolver = staticResolver;
    this.root = new MappingNode();
    this.requestFilters = new ArrayList<>();
    this.responseFilters = new ArrayList<>();
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
    // locate request/response filters
    OrderedComparator comparator = new OrderedComparator();
    factory.findBeansMatching(RequestFilter.class)
      .map(definition -> definition.instance(factory))
      .map(RequestFilter.class::cast)
      .collect(Collectors.toCollection(() -> requestFilters))
      .sort(comparator);
    requestFilters.forEach(filter -> log.info("Registered request filter '{}'", filter.getClass().getName()));
    factory.findBeansMatching(ResponseFilter.class)
      .map(definition -> definition.instance(factory))
      .map(ResponseFilter.class::cast)
      .collect(Collectors.toCollection(() -> responseFilters))
      .sort(comparator);
    responseFilters.forEach(filter -> log.info("Registered response filter '{}'", filter.getClass().getName()));
    // register definitions
    log.info("Found {} beans registering endpoints", endpointBeans.size());
    for (BeanDefinition<?> definition : endpointBeans) {
      Class<?> clazz = definition.getClazz();
      RestResource config = clazz.getAnnotation(RestResource.class);
      String basePath = StringOps.joinPaths(rootPath, config.path());
      List<Method> methods = Arrays.stream(clazz.getMethods())
        .filter(m -> m.isAnnotationPresent(Endpoint.class))
        .collect(Collectors.toList());
      for (Method method : methods) {
        Endpoint endpoint = method.getAnnotation(Endpoint.class);
        String endpointPath = StringOps.joinPaths(basePath, endpoint.path());
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

  /**
   * Handles the provided exchange. This method will resolve a response for the exchange, and write the response.
   * @param exchange the exchange to process.
   * @throws IOException on exchange writing errors.
   */
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

  /**
   * Resolves a request exchange into a response value for the server to return.
   * @param exchange the current http exchange to process.
   * @return the generated response from the node, if any.
   */
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
      if (node != null) {
        node.placeholderNames.forEach(variable -> pathVariables.put(variable, currentSegment));
      } else {
        break;
      }
    }
    if (node == null) {
      Response response = staticResolver.resolveResource(path);
      if (response.getStatus() == 404) {
        log.info("Node for found: the path '{}' is not mapped", path);
      }
      return response;
    } else {
      HttpMethod requestMethod = HttpMethod // extract the request method to resolve the processor.
        .parseFrom(exchange.getRequestMethod())
        .orElseThrow(() -> new RuntimeException("Invalid request method: " + exchange.getRequestMethod()));
      RequestProcessor requestProcessor = node.processors.get(requestMethod);
      if (requestProcessor == null) {
        return Response.notFound();
      } else {
        try {
          // initialize the current request info
          CurrentRequest.open(exchange);
          Response requestResponse = filterRequest(exchange);
          if (requestResponse == null) { // no request filter has overridden the response
            try {
              requestResponse = requestProcessor.process(exchange, pathVariables);
            } catch (InvocationTargetException e) {
              Throwable actualException = e.getCause();
              if (actualException == null) {
                requestResponse = Response.internalServerError(e);
              } else {
                requestResponse = exceptionMappers.stream()
                  .filter(mapper -> mapper.dealsWith(actualException))
                  .findFirst()
                  .map(mapper -> mapper.getResponse(actualException))
                  .orElse(Response.requestError(actualException));
              }
            }
          }
          // apply available response filters to the rest resource response (or the error value)
          return filterResponse(requestResponse, exchange);
        } finally {
          CurrentRequest.close(); // close the request in any case
        }
      }
    }
  }

  /**
   * Applies all request filters the the current exchange.
   * @param currentExchange the exchange being processed.
   * @return the filtered response, if any filter has provided a response override.
   */
  private Response filterRequest(HttpExchange currentExchange) {
    Response response = null;
    Iterator<RequestFilter> filters = requestFilters.iterator();
    while (response == null && filters.hasNext()) {
      response = filters.next().filterRequest(currentExchange);
    }
    return response;
  }

  /**
   * Filters the response using all registered response filters, and returning the final result.
   * @param currentResponse the initial response, as provided by the rest resource.
   * @param currentExchange the current exchange being processed.
   * @return the final response generated after applying all filters.
   */
  private Response filterResponse(Response currentResponse, HttpExchange currentExchange) {
    for (ResponseFilter filter : responseFilters) {
      Response filteredResponse = filter.filterResponse(currentResponse, currentExchange);
      if (filteredResponse != null) { // response should not be null, keep the last one if a filter returns null.
        currentResponse = filteredResponse;
      }
    }
    return currentResponse;
  }

  /**
   * Transforms a string defining a media type into an actual type class.
   * @param mediaType the media type to parse.
   * @return the generated value (which might be null).
   */
  private MediaTypeMapper resolveMapperForMediaType(String mediaType) {
    String effectiveMediaType = StringOps.isEmpty(mediaType) ? MediaType.TEXT_PLAIN : mediaType;
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
