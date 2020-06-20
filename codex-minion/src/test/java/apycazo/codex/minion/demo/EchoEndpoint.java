package apycazo.codex.minion.demo;

import apycazo.codex.minion.server.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Singleton
public class EchoEndpoint implements Endpoint {

  @Inject
  private ReportService reportService;

  @Override
  public String path() {
    return "/echo";
  }

  @Override
  public Stream<HttpMethod> allowedMethods() {
    return Stream.of(HttpMethod.GET);
  }

  @Override
  public ServerResponse process(ClientRequest request) {
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("from", EchoEndpoint.class.getName());
    responseBody.put("ts", Instant.now().getEpochSecond());
    responseBody.put("request", request.getRequestURI().toString());
    responseBody.put("results", reportService.getResults());
    return ServerResponse.builder().body(responseBody).build();
  }

  @Override
  public String accepts() {
    return null;
  }

  @Override
  public String contentType() {
    return ServerConstants.MEDIA_TYPE_JSON;
  }
}
