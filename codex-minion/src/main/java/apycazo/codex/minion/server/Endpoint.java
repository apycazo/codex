package apycazo.codex.minion.server;

import java.util.stream.Stream;

public interface Endpoint {

  String path();
  Stream<HttpMethod> allowedMethods();
  ServerResponse process(ClientRequest request);
  String accepts();
  String contentType();
  default boolean strictMatching() { return true; }
}
