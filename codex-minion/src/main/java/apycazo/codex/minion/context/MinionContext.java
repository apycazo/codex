package apycazo.codex.minion.context;

import apycazo.codex.minion.common.CommonUtils;
import apycazo.codex.minion.common.CoreException;
import apycazo.codex.minion.common.StatusCode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO:
 * - @Config: define singletons and prototypes by config
 * - @Properties: map property sources
 */
@Slf4j
public class MinionContext {

  @Getter
  private final Catalog catalog;
  @Getter
  private final BeanFactory factory;
  private final Properties properties;

  public MinionContext(String ... basePackagesToScan) {
    this(Collections.emptySet(), basePackagesToScan);
  }

  public MinionContext(Set<String> propertySources, String ... basePackagesToScan) {
    // init properties
    properties = new Properties();
    propertySources.forEach(source -> CommonUtils.readPropertiesFrom(source).ifPresent(properties::putAll));
    // create the catalog and the bean factory
    catalog = new Catalog(properties, this);
    factory = new BeanFactory(catalog);
    try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(basePackagesToScan).scan()) {
      // find and instance singletons (eager-init, prototypes are created on demand).
      List<Class<?>> candidates = scanResult
        .getClassesWithAnnotation(Singleton.class.getName())
        .stream()
        .map(this::resolveClass)
        .collect(Collectors.toList());
      factory.singletons(candidates);
    }
  }

  private Class<?> resolveClass(ClassInfo classInfo) {
    try {
      return Class.forName(classInfo.getName());
    } catch (ClassNotFoundException e) {
      log.error("Class for name '{}' returned not found", classInfo.getName());
      throw new CoreException(StatusCode.CLASSNAME_NOT_FOUND);
    }
  }
}
