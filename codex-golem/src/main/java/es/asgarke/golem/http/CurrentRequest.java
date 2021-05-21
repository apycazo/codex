package es.asgarke.golem.http;

import com.sun.net.httpserver.HttpExchange;
import es.asgarke.golem.tools.StringOps;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CurrentRequest {

  private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();
  private enum BasicKeys {

    RequestID("$requestId"), Exchange("$exchange");

    String strValue;

    BasicKeys(String strValue) {
      this.strValue = strValue;
    }

    String getValue() {
      return strValue;
    }
  }

  static void open(HttpExchange exchange) {
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put(BasicKeys.Exchange.getValue(), exchange);
    dataMap.put(BasicKeys.RequestID.getValue(), UUID.randomUUID().toString().substring(24));
    threadLocal.set(dataMap);
  }

  static void close() {
    threadLocal.remove();
  }

  public static <T> Optional<T> getAttribute(String key, Class<T> clazz) {
    Map<String, Object> attributes = threadLocal.get();
    Object object = attributes.get(key);
    if (object != null && object.getClass().isAssignableFrom(clazz)) {
      return Optional.of(clazz.cast(object));
    } else {
      return Optional.empty();
    }
  }

  public static String getId() {
    return getAttribute(BasicKeys.RequestID.getValue(), String.class).orElse(null);
  }

  public static HttpExchange getHttpExchange() {
    return getAttribute(BasicKeys.Exchange.getValue(), HttpExchange.class).orElse(null);
  }

  public static void setAttribute(String key, Object value) {
    if (!StringOps.isEmpty(key) && value != null) {
      Map<String, Object> attributes = threadLocal.get();
      attributes.put(key, value);
    }
  }

}
