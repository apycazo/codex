package es.asgarke.golem.core;

import es.asgarke.golem.core.annotations.Configuration;
import es.asgarke.golem.core.constructors.BeanDefinition;
import es.asgarke.golem.core.constructors.ConstructorBeanDefinition;
import es.asgarke.golem.core.constructors.ContextBeanDefinition;
import es.asgarke.golem.core.constructors.MethodBeanDefinition;
import es.asgarke.golem.core.definitions.BeanType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class BeanFactory {

  @Getter
  private final BeanProperties properties;
  private final Map<String, BeanDefinition<?>> definitionMap;
  private final ConditionResolver conditionResolver;
  private final ContextScanner scanner;
  private boolean isInitialized = false;

  public BeanFactory(GolemContext context) {
    properties = new BeanProperties();
    definitionMap = new LinkedHashMap<>();
    conditionResolver = new ConditionResolver(properties);
    scanner = new ContextScanner();
    if (context != null) {
      BeanDefinition<?> definition = new ContextBeanDefinition(context);
      definitionMap.put(definition.getName(), definition);
    }
  }

  public synchronized void initialize(Class<?>... classes) {
    if (isInitialized) {
      log.warn("Factory has already been initialized (can only be done once)");
    } else {
      log.info("Initializing context factory");
      scanner.scan(classes);
      scanner.getConfigurationSet().forEach(this::registerConfiguration);
      scanner.getSingletonSet().forEach(this::registerSingleton);
      log.info("Bean factory registration completed, initializing beans");
      getDefinitionStream().forEach(def -> {
        if (def.isLazy()) {
          log.debug("Skipping default initialization of lazy bean {}", def.fullName());
        } else {
          log.debug("Eager initialization of bean {}", def.fullName());
          def.instance(this);
        }
      });
      log.info("Bean initialization completed");
      isInitialized = true;
    }
  }

  private void registerConfiguration(Class<?> configClazz) {
    if (!Inspector.isValidBeanDefinition(configClazz)) {
      log.error("Invalid bean definition for class {}", configClazz);
      throw new RuntimeException("Invalid bean definition");
    }
    Configuration annotation = configClazz.getAnnotation(Configuration.class);
    // read properties
    for (String path : annotation.propertySources()) {
      properties.mergePropertiesFrom(path, true);
    }
    for (String path : annotation.optionalPropertySources()) {
      properties.mergePropertiesFrom(path, false);
    }
    // register definitions
    Set<Method> beanMethods = Stream.of(configClazz.getMethods())
      .filter(method -> method.isAnnotationPresent(Singleton.class))
      .collect(Collectors.toSet());
    if (!beanMethods.isEmpty()) {
      try {
        Object instance = configClazz.getDeclaredConstructor().newInstance();
        for (Method method : beanMethods) {
          String name = Inspector.getBeanName(method);
          if (definitionMap.containsKey(name)) {
            throw new RuntimeException("Duplicated bean name: " + name);
          }
          Class<?> type = method.getReturnType();
          log.debug("Getting definition for class {} through method {}", type, method.getName());
          BeanDefinition<?> definition = MethodBeanDefinition.forClassMethod(type, method, instance);
          if (conditionResolver.definitionMatchesConditions(definition, method)) {
            definitionMap.put(name, definition);
            log.debug("Registered bean definition {} ({})", type.getName(), name);
          } else {
            log.debug("Skipping bean '{}' because of non matching conditions", definition.getClazz());
          }
        }
      } catch (Exception e) {
        String msg = "Failed to instance base object for config class '" + configClazz.getName() + "'";
        log.error(msg);
        throw new RuntimeException(msg, e);
      }
    }
  }

  public BeanDefinition<?> registerSingleton(Class<?> clazz) {
    String name = Inspector.getBeanName(clazz);
    if (!Inspector.isValidBeanDefinition(clazz)) {
      log.error("Invalid bean definition for class {}", clazz);
      throw new RuntimeException("Invalid bean definition");
    } else if (definitionMap.containsKey(name)) {
      throw new RuntimeException("Duplicated bean name: " + name);
    } else {
      log.debug("Getting definition for class {}", clazz);
      BeanDefinition<?> definition = ConstructorBeanDefinition.forClass(clazz);
      if (conditionResolver.definitionMatchesConditions(definition)) {
        definitionMap.put(name, definition);
        log.debug("Registered bean definition {} ({})", clazz.getName(), name);
        return definition;
      } else {
        log.info("Skipping bean '{}' because of non matching conditions", definition.getClazz());
        return null;
      }
    }
  }

  /**
   * This method is only called from the server instance to register the special 'RestResource'
   * annotated classes. For this we need to re-scan the packages, and consider the following: <br>
   * <ul>
   *   <li>The beans detected are to be generated and injected like regular beans</li>
   *   <li>The resulting definitions are to be returned by this method, so the mapper can configure handling.</li>
   * </ul>
   * @return the definition list.
   */
  public List<BeanDefinition<?>> registerRestResources() {
    List<BeanDefinition<?>> resources = new ArrayList<>();
    scanner.getRestResourceSet().forEach(resource -> {
      BeanDefinition<?> definition = registerSingleton(resource);
      if (definition != null) {
        definition.setBeanType(BeanType.RestResource);
        resources.add(definition);
      }
    });
    return resources;
  }

  public Stream<BeanDefinition<?>> getDefinitionStream() {
    return definitionMap.values().stream();
  }

  public Optional<?> resolveBean(String name) {
    if (definitionMap.containsKey(name)) {
      return Optional.of(definitionMap.get(name).instance(this));
    } else {
      return Optional.empty();
    }
  }

  public <T> Optional<T> resolveBean(Class<T> clazz, String name) {
    if (name == null || name.isBlank()) {
      return resolveBean(clazz);
    } else if (!definitionMap.containsKey(name)) {
      return Optional.empty();
    } else {
      BeanDefinition<?> beanDefinition = definitionMap.get(name);
      if (!beanDefinition.canBeCastTo(clazz)) {
        String msg = "Incompatible definitions for name '" + name + "', actual class is "
          + beanDefinition.getClazz().getName();
        log.warn(msg);
        return Optional.empty();
      } else {
        Object instance = beanDefinition.instance(this);
        return Optional.of(clazz.cast(instance));
      }
    }
  }

  public <T> Optional<T> resolveBean(Class<T> clazz) {
    // find all valid matches
    List<BeanDefinition<?>> matches = getDefinitionStream()
      .filter(def -> def.canBeCastTo(clazz))
      .collect(Collectors.toList());
    if (matches.isEmpty()) {
      return Optional.empty();
    } else if (matches.size() == 1) {
      BeanDefinition<?> beanDefinition = matches.get(0);
      Object instance = beanDefinition.instance(this);
      return Optional.ofNullable(clazz.cast(instance));
    } else {
      // try to find an exact match
      Optional<BeanDefinition<?>> exactMatch = matches.stream()
        .filter(def -> def.getClazz() == clazz)
        .findFirst();
      if (exactMatch.isPresent()) {
        return Optional.ofNullable(clazz.cast(exactMatch.get().instance(this)));
      } else {
        // check for a primary value
        List<BeanDefinition<?>> options = matches.stream()
          .filter(BeanDefinition::isPrimary)
          .collect(Collectors.toList());
        if (options.size() == 1) {
          return Optional.of(clazz.cast(options.get(0).instance(this)));
        } else {
          String msg = "Unable to resolve exact match for class " + clazz.getName() + ", Options include: "
            + matches.stream().map(v -> v.getClazz().getName()).collect(Collectors.joining(", "));
          log.warn(msg);
          return Optional.empty();
        }
      }
    }
  }

  /**
   * Finds all beans which can be cast into the provided class, and return a stream of results.
   * @param clazz the class we want the beans to be casted into.
   * @return the stream with the results.
   */
  public Stream<BeanDefinition<?>> findBeansMatching(Class<?> clazz) {
    return getDefinitionStream().filter(def -> def.canBeCastTo(clazz));
  }

  public List<BeanDefinition<?>> getRestResourceDefinitions() {
    return definitionMap.values().stream()
      .filter(definition -> definition.getBeanType() == BeanType.RestResource)
      .collect(Collectors.toList());
  }

  /**
   * Creates a report of the current factory status.
   * @return a list containing all lines generated for the report.
   */
  public List<String> report() {
    final List<String> lines = new ArrayList<>();
    lines.add("=> Report:: Scanned packages");
    lines.addAll(scanner.getPackagesToScan());
    lines.add("=> Report:: Property paths");
    lines.addAll(properties.getPathsStream().collect(Collectors.toSet()));
    lines.add("=> Report:: Property values");
    properties.getPropertyKeyStream()
      .forEach(key -> lines.add(String.format("%s = %s", key, properties.getProperty(key))));
    lines.add("=> Report:: Beans");
    definitionMap.values().forEach(definition -> lines.add(definition.toString()));
    lines.add("=> Report:: Complete");
    return lines;
  }
}
