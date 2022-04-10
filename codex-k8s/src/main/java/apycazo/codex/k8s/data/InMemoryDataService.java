package apycazo.codex.k8s.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDataService implements DataService {

  private final Map<String, String> map = new ConcurrentHashMap<>();

  @Override
  public String getByKey(String key) {
    return map.get(key);
  }

  @Override
  public void saveOrUpdate(String key, String value) {
    map.put(key, value);
  }

  @Override
  public void deleteKey(String key) {
    map.remove(key);
  }

  @Override
  public Set<String> getKeys() {
    return map.keySet();
  }

  @Override
  public List<String> getValues() {
    return new ArrayList<>(map.values());
  }

  @Override
  public long getCount() {
    return map.size();
  }

  @Override
  public void delete() {
    map.clear();
  }

  @Override
  public String implementation() {
    return "in-memory";
  }
}
