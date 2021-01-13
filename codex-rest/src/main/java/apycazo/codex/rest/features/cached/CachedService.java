package apycazo.codex.rest.features.cached;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CachedService {

  private final Map<String, CachedValue> store = new ConcurrentHashMap<>();
  private final CachedReport cachedReport = new CachedReport();

  @CacheEvict(value = "default", key = "#cachedValue.id")
  public void save(CachedValue cachedValue) {
    if (cachedValue != null) {
      store.put(cachedValue.getId(), cachedValue);
      log.info("Saved value: {}", cachedValue);
    }
    cachedReport.save();
  }

  @Cacheable(value = "default", key = "#id")
  public CachedValue getById(String id) {
    cachedReport.read();
    log.info("Query by ID: {}", id);
    return store.get(id);
  }

  @CacheEvict(value = "default", key = "#cachedValue.id")
  public void removeById(String id) {
    store.remove(id);
  }

  @CacheEvict(value = "default", allEntries = true)
  public void clear() {
    store.clear();
  }

  public CachedReport getReport() {
    return cachedReport;
  }
}
