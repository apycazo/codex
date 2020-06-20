package apycazo.codex.minion.context;

import apycazo.codex.minion.common.CommonUtils;
import apycazo.codex.minion.common.CoreException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static apycazo.codex.minion.common.StatusCode.*;

@Slf4j
public class Catalog {

  private final Map<String, BeanRecord> registry;
  private final Properties properties;

  public Catalog(Properties properties, Object ...instances) {
    this.properties = properties;
    this.registry = new ConcurrentHashMap<>();
    register(this);
    if (instances != null) {
      for (Object instance: instances) register(instance);
    }
  }

  public Stream<BeanRecord> records() {
    return registry.values().stream();
  }

  /**
   * Looks for an existing record in the catalog matching the class and name provided.
   * @param clazz the clazz to match (mandatory).
   * @param name if the required bean needs to have been registered with a specific name (nullable).
   * @return the fetch result.
   */
  public List<BeanRecord> fetchCandidates(Class<?> clazz, String name) {
    // first pass: find all records assignable to required value
    List<BeanRecord> candidates = registry.values().stream()
      .filter(current -> current.classAssignableTo(clazz))
      .collect(Collectors.toList());
    if (candidates.isEmpty()) {
      return Collections.emptyList();
    } else if (candidates.size() == 1) {
      // only one possible candidate
      BeanRecord candidate = candidates.get(0);
      // no name or name matching?
      if (CommonUtils.isEmptyOrBlank(name) || candidate.hasName(name)) {
        return candidates;
      } else {
        // no matching name
        return Collections.emptyList();
      }
    } else if (CommonUtils.isEmptyOrBlank(name)) {
      // multiple candidates
      return candidates.stream().filter(candidate -> candidate.classMatches(clazz)).collect(Collectors.toList());
    } else {
      // fetch a candidate by name
      return candidates.stream().filter(current -> current.hasName(name)).collect(Collectors.toList());
    }
  }

  public Catalog register(Object instance) {
    return register(instance, null);
  }

  public Catalog register(Object instance, String name) {
    String registryKey = instance.getClass().getName();
    name = CommonUtils.isEmptyOrBlank(name) ? registryKey : name;
    log.info("Registering key {} with name {}", registryKey, name);
    if (registry.containsKey(registryKey)) {
      log.error("Bean class '{}' already registered", registryKey);
      throw new CoreException(ALREADY_REGISTERED);
    } else {
      if (fetchByName(name).isPresent()) {
        log.error("Bean name '{}' already registered", name);
        throw new CoreException(ALREADY_REGISTERED);
      } else {
        registry.put(registryKey, BeanRecord.of(instance, name));
        return this;
      }
    }
  }

  private Optional<BeanRecord> fetchByName(String name) {
    return registry.values().stream().filter(record -> record.hasName(name)).findAny();
  }

  public <T> T getByClass(Class<? extends T> clazz) {
    BeanRecord beanRecord = registry.get(clazz.getName());
    return beanRecord == null ? null : beanRecord.getInstanceAs(clazz);
  }

  public Object getByName(String name) {
    return fetchByName(name).map(BeanRecord::getInstance).orElse(null);
  }

  public Optional<String> getProperty(String key) {
    return Optional.ofNullable(properties.getProperty(key));
  }
}
