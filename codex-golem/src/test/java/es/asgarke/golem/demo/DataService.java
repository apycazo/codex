package es.asgarke.golem.demo;

import es.asgarke.golem.tools.StringOps;
import es.asgarke.golem.types.Pair;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple data service as an example. Uses a concurrent hashmap to handle multiple requests and to make sure
 * null values can not be stored.
 */
@Singleton
public class DataService {

  private final Map<String, Object> store;

  public DataService() {
    store = new ConcurrentHashMap<>();
  }

  public int size() {
    return store.size();
  }

  public Map<String, Object> all() {
    return Collections.unmodifiableMap(store);
  }

  public Optional<Object> get(String key) {
    return Optional.ofNullable(store.get(key));
  }

  public boolean put(String key, Object value) {
    if (StringOps.isEmpty(key) || value == null) {
      return false;
    } else {
      store.put(key, value);
      return true;
    }
  }

  public int putAll(Collection<Pair<String, Object>> entries) {
    if (entries == null || entries.isEmpty()) {
      return 0;
    } else {
      int accepted = 0;
      for (Pair<String, Object> pair : entries) {
        accepted += put(pair.getLeft(), pair.getRight()) ? 1 : 0;
      }
      return accepted;
    }
  }

  public boolean exists(String key) {
    return store.containsKey(key);
  }

  public void remove(String key) {
    store.remove(key);
  }

  public void clear() {
    store.clear();
  }
}
