package apycazo.codex.rest.features.store;

import apycazo.codex.rest.common.error.ErrorCode;
import apycazo.codex.rest.common.error.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class StoreService {

  private final Map<String, Object> map = new ConcurrentHashMap<>();

  public int size() {
    return map.size();
  }

  public boolean containsKey(String key) {
    validateKey(key);
    return map.containsKey(key);
  }

  public void store(String key, Object value) {
    validateKey(key);
    map.put(key, value);
  }

  public Object getValue(String key) {
    validateKey(key);
    return map.get(key);
  }

  public boolean delete(String key) {
    validateKey(key);
    return map.remove(key) != null;
  }

  /**
   * Validates the store key provided, since the concurrent map implementation does not allow for null keys, and
   * also we want to avoid empty keys.
   * @param key the key to validate.
   * @throws ServiceException when the key is not valid.
   */
  private void validateKey(String key) {
    if (StringUtils.isEmpty(key)) {
      throw new ServiceException("invalid 'null' parameter", ErrorCode.InvalidParameter);
    }
  }
}
