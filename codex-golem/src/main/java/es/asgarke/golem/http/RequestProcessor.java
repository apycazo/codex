package es.asgarke.golem.http;

import com.sun.net.httpserver.HttpExchange;
import es.asgarke.golem.http.annotations.Body;
import es.asgarke.golem.http.annotations.PathParam;
import es.asgarke.golem.http.annotations.QueryParam;
import es.asgarke.golem.http.annotations.RequestHeader;
import es.asgarke.golem.http.definitions.HeaderKeys;
import es.asgarke.golem.http.types.MediaTypeMapper;
import es.asgarke.golem.http.types.Response;
import es.asgarke.golem.tools.ParserTool;
import es.asgarke.golem.tools.StringTool;
import es.asgarke.golem.types.MethodParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class RequestProcessor {

  private final String consumedMedia;
  private final String producedMedia;
  private final Method method;
  private final Object instance;
  private final MediaTypeMapper mapper;

  public Response process(HttpExchange exchange, Map<String, String> pathVariables) throws InvocationTargetException {
    // initialize the current request info
    CurrentRequest.open(exchange);
    // check media type
    if (!consumedMedia.isBlank()) {
      boolean matchingHeader = exchange.getRequestHeaders().get(HeaderKeys.CONTENT_TYPE)
        .stream()
        .anyMatch(v -> isMatchingHeader(v, consumedMedia));
      if (!matchingHeader) {
        return Response.mediaTypeError(consumedMedia);
      }
    }
    // process query
    Map<String, String> queryMap = generateQueryMap(exchange.getRequestURI().getQuery());
    // resolve required params
    try {
      Object[] args = new Object[method.getParameterCount()];
      MethodParam[] params = MethodParam.params(method);
      // get all param values required by the method
      for (int i = 0; i < method.getParameterCount(); i++) {
        MethodParam param = params[i];
        args[i] = resolveValue(param, pathVariables, queryMap, exchange);
      }
      Object result = method.invoke(instance, args);
      int status = exchange.getResponseCode() != -1 ? exchange.getResponseCode() : 200;
      if (result == null) {
        return Response.builder().status(status).build();
      } else if (result instanceof Response) {
        Response response = (Response) result;
        if (StringTool.isEmpty(response.getMediaType()) && !StringTool.isEmpty(producedMedia)) {
          response.setMediaType(producedMedia);
        }
        return response;
      } else {
        return Response.builder().status(status).content(result).mediaType(producedMedia).build();
      }
    } catch (IllegalAccessException e) {
      return Response.internalServerError(e);
    } catch (IOException e) {
      return Response.requestError(e);
    } finally {
      CurrentRequest.close(); // close the request in any case
    }
  }

  /**
   * Checks whether the header value matches the expectation. An example of this would be:
   * <li>value = application/json; charset=UTF-8</li>
   * <li>expected = application/json</li>
   *
   * @param value    the request header value to match.
   * @param expected the value we need to match to.
   * @return true when the value matches the expectation (this is case insensitive).
   */
  private boolean isMatchingHeader(String value, String expected) {
    if (value == null || value.isBlank() || expected == null || expected.isBlank()) {
      return false;
    } else {
      return Arrays.stream(value.split("\\s*;\\s*"))
        .anyMatch(v -> v.equalsIgnoreCase(expected));
    }
  }

  /**
   * Transforms a query string into a key-value map, to make it easier to recover values.
   *
   * @param query the original string query.
   * @return the generated map (can be empty if the query is blank).
   */
  private Map<String, String> generateQueryMap(String query) {
    Map<String, String> queryMap = new HashMap<>();
    if (query != null) {
      String[] pairs = query.split("&");
      for (String pair : pairs) {
        String[] keyValue = pair.split("=");
        if (keyValue.length > 0) {
          if (keyValue.length > 1) {
            queryMap.put(keyValue[0], keyValue[1]);
          } else {
            queryMap.put(keyValue[0], "");
          }
        }
      }
    }
    return queryMap;
  }

  private Object resolveValue(MethodParam param, Map<String, String> pathVariables, Map<String, String> queryMap,
    HttpExchange exchange) throws IOException {
    Optional<PathParam> pathParam = param.getAnnotation(PathParam.class);
    if (pathParam.isPresent()) {
      String varName = pathParam.get().value();
      if (!pathVariables.containsKey(varName)) {
        throw new RuntimeException("Invalid mapping value '" + varName + "'");
      } else {
        return ParserTool.parse(pathVariables.get(varName), param.getType());
      }
    }
    Optional<QueryParam> queryParam = param.getAnnotation(QueryParam.class);
    if (queryParam.isPresent()) {
      String varName = queryParam.get().value();
      return ParserTool.parse(queryMap.getOrDefault(varName, ""), param.getType());
    }
    Optional<Body> bodyParam = param.getAnnotation(Body.class);
    if (bodyParam.isPresent()) {
      if (mapper == null) {
        throw new RuntimeException("Request has a body, but no mapper has been registered");
      } else {
        return mapper.toObjectInstance(exchange.getRequestBody(), param.getType());
      }
    }
    // resolve headers, which can be a single string, or a list (this can throw errors when other types are used.
    Optional<RequestHeader> headerParam = param.getAnnotation(RequestHeader.class);
    if (headerParam.isPresent()) {
      String key = headerParam.get().value();
      List<String> strings = exchange.getRequestHeaders().get(key);
      if (param.getType() == String.class) {
        return String.join(",", strings);
      } else {
        return strings;
      }
    }
    // inject the current exchange if the method requires it.
    if (param.getType() == HttpExchange.class) {
      return exchange;
    }
    log.warn("Mapped method '{}' without a valid annotation or type", method.getName());
    return null;
  }
}
