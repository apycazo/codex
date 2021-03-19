package apycazo.codex.rest.common.filter;

import apycazo.codex.rest.common.util.IdGenerator;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A request-scoped bean. To use, <code>org.glassfish.jersey.server.spring.scope.RequestContextFilter</code> needs
 * to be registered in the ResourceConfig (in this case, see the <code>RestApplication</code> class).
 */
@Data
@Component
@RequestScope
public class RequestExtraData {

  private String requestId;
  private Map<String, Object> data;

  public RequestExtraData() {
    requestId = IdGenerator.generateId();
  }

  public RequestExtraData setRequestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  public Optional<Object> getData(String key) {
    if (data != null && data.containsKey(key)) {
      return Optional.of(data.get(key));
    } else {
      return Optional.empty();
    }
  }

  public <T> Optional<T> getData(String key, Class<T> clazz) {
    if (data != null && data.containsKey(key)) {
      Object value = data.get(key);
      T castedValue = clazz.cast(value);
      return Optional.of(castedValue);
    } else {
      return Optional.empty();
    }
  }

  public RequestExtraData putData(String key, Object value) {
    if (data == null) {
      data = new HashMap<>();
    }
    data.put(key, value);
    return this;
  }
}
