package apycazo.codex.minion.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class ClientRequest {

  private final ObjectMapper mapper;
  private final HttpExchange httpExchange;

  public ClientRequest(ObjectMapper mapper, HttpExchange httpExchange) {
    this.mapper = mapper;
    this.httpExchange = httpExchange;
  }

  public URI getRequestURI() {
    return httpExchange.getRequestURI();
  }

  public HttpMethod getHttpMethod() {
    return HttpMethod.parse(httpExchange.getRequestMethod());
  }

  // TODO: restore the inputStream when required! (use string as the base content)
  public <T> T parseBodyAs(Class<? extends T> clazz) throws IOException {
    InputStream requestBody = httpExchange.getRequestBody();
    return (T) mapper.readValue(requestBody, clazz);
  }

  public List<String> getHeader(String header) {
    return httpExchange.getRequestHeaders().get(header);
  }

  public HttpExchange getHttpExchange() {
    return httpExchange;
  }

}
