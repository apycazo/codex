package es.asgarke.golem.core;

import es.asgarke.golem.core.annotations.Configuration;
import es.asgarke.golem.http.annotations.RestResource;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Getter
public class ContextScanner {

  private final Set<String> packagesToScan = new HashSet<>();
  private final Set<Class<?>> configurationSet = new HashSet<>();
  private final Set<Class<?>> singletonSet = new HashSet<>();
  private final Set<Class<?>> restResourceSet = new HashSet<>();

  public synchronized void scan(Class<?>... classes) {
    scan(true, classes);
  }

  public synchronized void scan(boolean logReport, Class<?>... classes) {
    if (classes == null || classes.length == 0) {
      throw new RuntimeException("Invalid scan collection: can not be null nor empty");
    } else {
      // scan initial classes
      Arrays.stream(classes).forEach(this::processConfiguration);
      // scan packages
      int initialCount;
      int actualCount;
      do {
        initialCount = packagesToScan.size();
        String [] packages = packagesToScan.toArray(new String[0]);
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(packages).scan()) {
          filterByAnnotation(scanResult, Configuration.class).forEach(this::processConfiguration);
        }
        actualCount = packagesToScan.size();
      } while (initialCount < actualCount);
      // scan for beans now
      String [] packages = packagesToScan.toArray(new String[0]);
      try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(packages).scan()) {
        filterByAnnotation(scanResult, Singleton.class).stream()
          .collect(Collectors.toCollection(() -> singletonSet));
        filterByAnnotation(scanResult, RestResource.class).stream()
          .collect(Collectors.toCollection(() -> restResourceSet));
      }
    }
    if (logReport) {
      log.info("** Configuration scan report **");
      log.info("> Configuration classes registered: {}", configurationSet.size());
      configurationSet.forEach(clazz -> log.info(">> {}", clazz));
      log.info("> Packages to scan: {}", packagesToScan.size());
      packagesToScan.forEach(packagePath -> log.info(">> {}", packagePath));
      log.info("> Singletons: {}", singletonSet.size());
      singletonSet.forEach(clazz -> log.info(">> {}", clazz));
      log.info("> Rest resources: {}", restResourceSet.size());
      restResourceSet.forEach(clazz -> log.info(">> {}", clazz));
    }
  }

  /**
   * Filters the scan result by the annotation required.
   *
   * @param scanResult the scan result we are filtering.
   * @param clazz      the annotation class we are looking for.
   * @return the list of classes found to be annotated with the required element.
   */
  private List<Class<?>> filterByAnnotation(ScanResult scanResult, Class<? extends Annotation> clazz) {
    return scanResult
      .getClassesWithAnnotation(clazz.getName())
      .stream()
      .map(this::resolveClass)
      .collect(Collectors.toList());
  }

  /**
   * Returns a class from the name provided by the given class info instance.
   *
   * @param classInfo the info object to process.
   * @return the class generated from the name found.
   */
  private Class<?> resolveClass(ClassInfo classInfo) {
    try {
      return Class.forName(classInfo.getName());
    } catch (ClassNotFoundException e) {
      String msg = String.format("Class for name '%s' returned not found", classInfo.getName());
      log.error(msg);
      throw new RuntimeException(msg);
    }
  }

  /**
   * Processes a configuration class, adding required package paths to the existing set and making sure any extra
   * configurations and beans imported are added to the existing inventory.
   * @param configClass the config class we are processing.
   */
  private void processConfiguration(Class<?> configClass) {
    if (configClass.isAnnotationPresent(Configuration.class) && !configurationSet.contains(configClass)) {
      log.debug("Processing config class {}", configClass);
      Configuration annotation = configClass.getAnnotation(Configuration.class);
      configurationSet.add(configClass);
      Stream // add package paths to the scan
        .of(annotation.scanPaths())
        .collect(Collectors.toCollection(() -> packagesToScan));
      Stream // add package class paths to the scan
        .of(annotation.scanPackages())
        .map(Class::getPackageName)
        .collect(Collectors.toCollection(() -> packagesToScan));
      // if no packages were specified use the config class package
      if (packagesToScan.isEmpty()) {
        packagesToScan.add(configClass.getPackageName());
      }
      // manage any imports
      Class<?>[] importedClasses = annotation.importDefinitions();
      for (Class<?> importedClass : importedClasses) {
        if (importedClass.isAnnotationPresent(Configuration.class)) {
          log.debug("{} imports configuration {}", configClass.getSimpleName(), importedClass);
          processConfiguration(importedClass);
        } else if (importedClass.isAnnotationPresent(Singleton.class)) {
          log.debug("{} imports singleton {}", configClass.getSimpleName(), importedClass);
          singletonSet.add(importedClass);
        } else {
          log.warn("{} imports class {} (nothing to do, must be @Configuration of @Singleton annotated)",
            configClass.getSimpleName(), importedClass);
        }
      }
    }
  }
}
