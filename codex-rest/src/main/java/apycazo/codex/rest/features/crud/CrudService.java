package apycazo.codex.rest.features.crud;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple key-value storage.
 */
@Service
public class CrudService {

  private final Map<String, String> valueStorage = new HashMap<>();

  public String getValueForKey(String key) {
    return valueStorage.get(key);
  }

  public void setValueForKey(String key, String value) {
    valueStorage.put(key, value);
  }

  public int getSize() {
    return valueStorage.size();
  }

  public boolean isKeyPresent(String key) {
    return valueStorage.containsKey(key);
  }

  public void deleteKey(String key) {
    valueStorage.remove(key);
  }

  public void clear() {
    valueStorage.clear();
  }

  public Map<String, String> getAll() {
    return new HashMap<>(valueStorage);
  }
}
