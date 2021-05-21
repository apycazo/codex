package es.asgarke.golem.demo;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import es.asgarke.golem.http.CurrentRequest;
import es.asgarke.golem.http.RequestFilter;
import es.asgarke.golem.http.ResponseFilter;
import es.asgarke.golem.http.types.Response;

import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class IdRequestFilter implements RequestFilter, ResponseFilter {

  public static final String ID_ATTRIBUTE_NAME = "x-request-id";

  @Override
  public Response filterRequest(HttpExchange exchange) {
    Headers requestHeaders = exchange.getRequestHeaders();
    String id = requestHeaders.containsKey(ID_ATTRIBUTE_NAME)
      ? requestHeaders.getFirst(ID_ATTRIBUTE_NAME)
      : UUID.randomUUID().toString().substring(24);
    CurrentRequest.setAttribute(ID_ATTRIBUTE_NAME, id);
    return null;
  }

  @Override
  public Response filterResponse(Response currentResponse, HttpExchange exchange) {
    String id = CurrentRequest.getAttribute(ID_ATTRIBUTE_NAME, String.class).orElse("unknown");
    exchange.getResponseHeaders().add(ID_ATTRIBUTE_NAME, id);
    return currentResponse;
  }

  @Override
  public Integer getOrder() {
    return RequestFilter.super.getOrder();
  }
}
