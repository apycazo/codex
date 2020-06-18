package apycazo.codex.minion.server;

import apycazo.codex.minion.common.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

import static apycazo.codex.minion.server.ServerConstants.HEADER_CONTENT_TYPE;

@Slf4j
public class RequestHandler implements HttpHandler {

  private final Endpoint endpoint;
  private final ObjectMapper mapper;

  public RequestHandler(Endpoint endpoint, ObjectMapper mapper) {
    this.endpoint = endpoint;
    this.mapper = mapper;
  }

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    try {
      ClientRequest request = new ClientRequest(mapper, httpExchange);
      if (!isMatchingEndpoint(request)) {
        httpExchange.sendResponseHeaders(404, 0);
      } else {
        ServerResponse response = endpoint.process(request);
        Object body = response.body();
        byte[] bodyBytes = new byte[0];
        if (body != null) {
          if (body instanceof String) bodyBytes = ((String)body).getBytes();
          else {
            bodyBytes = mapper.writeValueAsBytes(body);
          }
        }
        httpExchange.sendResponseHeaders(response.status(), bodyBytes.length);
        String mediaType = response.mediaType();
        if (!CommonUtils.isEmptyOrBlank(mediaType)) {
          httpExchange.getResponseHeaders().set(HEADER_CONTENT_TYPE, mediaType);
        }
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(bodyBytes);
        outputStream.flush();
      }
    } catch (IOException e) {
      log.error("IOException processing {}", httpExchange.getRequestURI(), e);
      httpExchange.sendResponseHeaders(500, 0); // throws an IOException too
    }
  }

  private boolean isMatchingEndpoint(ClientRequest request) {
    HttpMethod httpMethod = request.getHttpMethod();
    if (endpoint.allowedMethods().anyMatch(method -> method == httpMethod)) {
      return !endpoint.strictMatching() || request.getRequestURI().toString().endsWith(endpoint.path());
    } else {
      return false;
    }
  }
}
