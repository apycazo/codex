package apycazo.codex.minion.server;

import apycazo.codex.minion.common.CommonUtils;

public enum HttpMethod {

  GET, PUT, POST, DELETE, OPTIONS, HEAD, UNKNOWN;

  public static HttpMethod parse(String method) {
    if (CommonUtils.isEmptyOrBlank(method)) return UNKNOWN;
    switch (method.toUpperCase()) {
      case "GET": return GET;
      case "PUT": return PUT;
      case "POST": return POST;
      case "DELETE": return DELETE;
      case "OPTIONS": return OPTIONS;
      case "HEAD": return HEAD;
      default: return UNKNOWN;
    }
  }
}
