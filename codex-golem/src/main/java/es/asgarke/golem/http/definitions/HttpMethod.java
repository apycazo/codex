package es.asgarke.golem.http.definitions;

import es.asgarke.golem.tools.StringOps;

import java.util.Optional;
import java.util.stream.Stream;

public enum HttpMethod {

  GET, PUT, POST, DELETE, HEAD, PATCH;

  public static Optional<HttpMethod> parseFrom(String s) {
    if (StringOps.isEmpty(s)) {
      return Optional.empty();
    } else {
      return Stream.of(HttpMethod.values()).filter(v -> v.name().equalsIgnoreCase(s.trim())).findFirst();
    }
  }
}
