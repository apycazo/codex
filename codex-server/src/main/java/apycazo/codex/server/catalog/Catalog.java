package apycazo.codex.server.catalog;

import apycazo.codex.server.AppContext;
import apycazo.codex.server.annotations.Prototype;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// scans and holds instances of components
@Slf4j
public class Catalog {

  private final Map<String, CatalogEntry> records;

  public Catalog() {
    records = new ConcurrentHashMap<>();
  }

  public Catalog(AppContext appContext) {
    this();
    String beanName = resolveClassName(AppContext.class);
    records.put(beanName, new CatalogEntry(appContext));
  }

  // TODO: ¿que ocurre si no se pueden generar todas la inyecciones que se piden?
  // TODO: Optional injections
  // TODO: calls over prototype classes?
  public <T> T singleton(Class<? extends T> clazz) {
    return singleton(clazz, null);
  }

  public <T> T singleton(Class<? extends T> clazz, String requiredBy) {
    log.info("Requested singleton of class {}", clazz.getName());
    Optional<CatalogEntry> cachedResult = findEntryByClass(clazz);
    if (cachedResult.isPresent()) {
      CatalogEntry entry = cachedResult.get();
      if (requiredBy != null) {
        entry.addReference(requiredBy);
      }
      return clazz.cast(entry.getInstance());
    } else {
      return instanceBean(clazz, true, requiredBy);
    }
  }

  private <T> Optional<CatalogEntry> findEntryByClass(Class<? extends T> clazz) {
    Optional<CatalogEntry> exactResult = records.values().stream()
      .filter(v -> v.getInstanceClass().getName().equals(clazz.getName()))
      .findFirst();
    if (exactResult.isPresent()) {
      return exactResult;
    } else {
      return records.values().stream()
        .filter(v -> v.getInstanceClass().isAssignableFrom(clazz))
        .findFirst();
    }
  }

  private <T> Optional<? extends T> findByClass(Class<? extends T> clazz) {
    return findEntryByClass(clazz).map(clazz::cast);
  }

  public <T> T prototype(Class<? extends T> clazz) {
    return instanceBean(clazz, false, null);
  }

  private <T> T instanceBean(Class<? extends T> clazz, boolean isSingleton, String requiredBy) {
    log.info("Constructing new instance of {}", clazz.getName());
    T instance;
    try {
      instance = clazz.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      log.error("Clazz '{}' can not be instanced", clazz.getName(), e);
      throw new RuntimeException("Unable to continue");
    }
    String beanName = resolveClassName(clazz);
    List<Field> injectionFields = Stream.of(instance.getClass().getDeclaredFields())
      .filter(field -> field.isAnnotationPresent(Inject.class))
      .collect(Collectors.toList());
    for (Field field : injectionFields) {
      try {
        field.setAccessible(true);
        if (field.isAnnotationPresent(Prototype.class)) {
          // the field requires the injection to use a prototype.
          field.set(instance, prototype(field.getType()));
        } else if (field.getType().isAnnotationPresent(Prototype.class)) {
          // the injected class is annotated as prototype.
          field.set(instance, prototype(field.getType()));
        } else {
          // by default, the class is a singleton.
          Object singleton = singleton(field.getType(), beanName);
          field.set(instance, singleton);
        }
        field.setAccessible(false);
      } catch (Exception e) {
        log.error("Failed to set instance {} field", clazz.getName(), e);
        throw new RuntimeException("Unable to continue");
      }
    }
    CatalogEntry catalogEntry = new CatalogEntry(instance, requiredBy);
    if (isSingleton) {
      if (clazz.isAnnotationPresent(Prototype.class)) {
        log.warn("Caution! created a singleton of a class annotated as 'prototype'");
      }
      records.put(beanName, catalogEntry);
    } else if (clazz.isAnnotationPresent(Singleton.class)) {
      log.warn("Caution! created a prototype of a class annotated as 'singleton'");
    }
    // initialize, if required
    catalogEntry.initialize();
    return instance;
  }

  public Map<String, Class<?>> inventory() {
    Map<String, Class<?>> inventory = new HashMap<>();
    records.keySet().forEach(name -> inventory.put(name, records.get(name).getInstanceClass()));
    return inventory;
  }

  private String resolveClassName(Class<?> clazz) {
    if (clazz.isAnnotationPresent(Named.class)) {
      String name = clazz.getDeclaredAnnotation(Named.class).value();
      if (name.trim().isEmpty()) {
        log.warn("Caution! attempt to name a bean with an empty string");
        return clazz.getName();
      } else {
        return name;
      }
    } else {
      return clazz.getName();
    }
  }
}
