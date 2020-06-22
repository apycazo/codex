package apycazo.codex.minion.context;

import apycazo.codex.minion.common.CommonUtils;
import apycazo.codex.minion.common.CoreException;
import apycazo.codex.minion.common.StatusCode;
import apycazo.codex.minion.context.catalog.BeanRecord;
import apycazo.codex.minion.context.catalog.Catalog;
import apycazo.codex.minion.context.conditions.ConditionResolver;
import apycazo.codex.minion.context.conditions.OnPropertyCondition;
import apycazo.codex.minion.context.properties.PropertySource;
import apycazo.codex.minion.context.properties.PropertySources;
import apycazo.codex.minion.context.properties.PropertyValue;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static apycazo.codex.minion.common.StatusCode.*;

/**
 * Defines the main dependency injection context, being tasked with bean instancing and dependency injection.
 */
@Slf4j
public class MinionContext {

  @Getter
  private final Catalog catalog;
  @Getter
  private final Properties properties;
  private final Set<String> propertySources;
  private final String[] basePackages;
  private boolean started;

  /**
   * Configures a new context, given a set of property sources and base packages to look for bean definitions.
   * @param basePackagesToScan a var args array of packages defining where to look to find bean definitions.
   */
  public MinionContext(String... basePackagesToScan) {
    this(Collections.emptySet(), basePackagesToScan);
  }

  /**
   * Configures a new context, given a set of property sources and base packages to look for bean definitions.
   * @param propertySources a set of property sources to use by default.
   * @param basePackagesToScan a var args array of packages defining where to look to find bean definitions.
   */
  public MinionContext(Set<String> propertySources, String... basePackagesToScan) {
    this.started = false;
    this.basePackages = basePackagesToScan == null
      ? new String[0]
      : Arrays.copyOf(basePackagesToScan, basePackagesToScan.length);
    this.propertySources = Collections.unmodifiableSet(propertySources);
    this.properties = new Properties();
    // create a new catalog with the existing property instance, and registers itself.
    this.catalog = new Catalog(properties, this);
  }

  /**
   * Starts the context with the existing configuration. This method will scan for any bean available and resolve
   * whatever injections are required. The beans references are included into an internal catalog. This instance can be
   * injected into other beans.
   * @return the generated context.
   */
  public MinionContext start() {
    if (started) {
      log.warn("Context already started!");
    } else {
      resolveProperties(); // runs the initial property resolution, config providers might inject more values.
      // scan packages
      try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(basePackages).scan()) {
        // scan and instance bean providers
        configProviders(filterByAnnotation(scanResult, ConfigProvider.class));
        // scan and instance singletons
        singletons(filterByAnnotation(scanResult, Singleton.class));
        // resolve catalog injections
        resolveInjections();
        // init beans
        catalog.records().map(BeanRecord::getInstance).forEach(this::init);
      }
      started = true;
    }
    return this;
  }

  /**
   * Creates an instance of the provided class and resolves any injections required.
   * @param clazz the class to instance and inject.
   * @param <T> the class type to respond with.
   * @return the resolved bean.
   */
  public <T> T prototype(Class<? extends T> clazz) {
    T instance = instance(clazz);
    Stream.of(instance.getClass().getDeclaredFields())
      .filter(field -> field.isAnnotationPresent(Inject.class))
      .forEach(field -> injectDependency(instance, field));
    return instance;
  }

  /**
   * Filters the scan result by the annotation required.
   * @param scanResult the scan result we are filtering.
   * @param clazz the annotation class we are looking for.
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
   * @param classInfo the info object to process.
   * @return the class generated from the name found.
   */
  private Class<?> resolveClass(ClassInfo classInfo) {
    try {
      return Class.forName(classInfo.getName());
    } catch (ClassNotFoundException e) {
      log.error("Class for name '{}' returned not found", classInfo.getName());
      throw new CoreException(StatusCode.CLASSNAME_NOT_FOUND);
    }
  }

  /**
   * Creates a simple instance of the required class, calling the constructor.
   * @param clazz the class se are instancing.
   * @param <T> the actual class type we are trying to return.
   * @return the instance created.
   */
  private <T> T instance(Class<? extends T> clazz) {
    if (clazz.isInterface()) {
      log.error("Interfaces are not allowed as beans");
      throw new CoreException(BEAN_IS_INTERFACE);
    }
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new CoreException(BEAN_INSTANCE_FAILED);
    }
  }

  /**
   * Evaluates the provided class list as config provider elements. Notice that the provider instance is not registered,
   * as are all the methods annotated with @Singleton.<p></p>
   * Providers do not allow for field injection, and will throw an exception if an attempt pf this is made. Also notice
   * that providers are always instanced, not allowing any condition annotation
   * @param classList the classes to process as configuration providers.
   */
  private void configProviders(List<Class<?>> classList) {
    List<Class<? extends Annotation>> forbiddenAnnotations = Stream
      .of(Singleton.class, Prototype.class, PropertyValue.class, OnPropertyCondition.class)
      .collect(Collectors.toList());
    for (Class<?> clazz : classList) {
      if (Stream.of(clazz.getDeclaredFields())
        .anyMatch(field -> CommonUtils.isAnnotationPresent(field, forbiddenAnnotations))) {
        log.error("Injections are not allowed on bean providers");
        throw new CoreException(BEAN_PROVIDER_INJECTIONS);
      } else {
        // inject any extra property sources declared here
        if (clazz.isAnnotationPresent(PropertySource.class)) {
          PropertySource propertySource = clazz.getAnnotation(PropertySource.class);
          resolveProperties(propertySource.location(), propertySource.mandatory());
        } // both annotations can be used together.
        if (clazz.isAnnotationPresent(PropertySources.class)) {
          PropertySource[] propertySources = clazz.getAnnotation(PropertySources.class).value();
          for (PropertySource propertySource : propertySources) {
            resolveProperties(propertySource.location(), propertySource.mandatory());
          }
        }
        Object instance = instance(clazz);
        List<Method> methods = Stream.of(clazz.getDeclaredMethods())
          .filter(method -> method.isAnnotationPresent(Singleton.class))
          .filter(method -> ConditionResolver.matchesConditions(catalog, method))
          .collect(Collectors.toList());
        for (Method method : methods) {
          if (method.isAnnotationPresent(Singleton.class)) {
            if (method.getParameters().length != 0) {
              log.error("Bean provider methods cannot have parameters");
              throw new CoreException(OPERATION_NOT_SUPPORTED);
            } else if (method.getReturnType().getName().equals(Void.class.getName())) {
              log.error("Bean provider methods can not return void");
              throw new CoreException(OPERATION_NOT_SUPPORTED);
            } else {
              try {
                Object bean = method.invoke(instance);
                catalog.register(bean, method.getName());
              } catch (Exception e) {
                log.error("Failed to invoke bean provider method", e);
                throw new CoreException(INVOCATION_ERROR);
              }
            }
          } else if (method.isAnnotationPresent(Prototype.class)) {
            log.warn("Bean providers do not support prototypes");
          }
        }
      }
    }
  }

  /**
   * Instances and registers all classes provided as singletons.
   * @param classList the classes to instance as singletons.
   */
  private void singletons(List<Class<?>> classList) {
    for (Class<?> clazz : ConditionResolver.filterByCondition(catalog, classList)) {
      Object instance = instance(clazz);
      String name = Optional // resolve the instance name, defaulting to the class name
        .ofNullable(clazz.getAnnotation(Named.class))
        .map(Named::value).orElse(clazz.getName());
      catalog.register(instance, name);
    }
  }

  /**
   * Resolves all injections required by the currently instanced beans found in the catalog.
   */
  private void resolveInjections() {
    catalog.records().forEach(record -> {
      Object instance = record.getInstance();
      Stream.of(instance.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(PropertyValue.class))
        .forEach(field -> injectDependency(instance, field));
    });
  }

  /**
   * Injects whatever value is required by the given field. Notice that fields with more than one valid annotation
   * will not throw an error, but only the first annotation will be considered.
   * @param instance the instance we are trying to inject into.
   * @param field the current injection field required.
   */
  private void injectDependency(Object instance, Field field) {
    try {
      field.setAccessible(true);
      Class<?> beanClass = field.getType();
      if (field.isAnnotationPresent(PropertyValue.class)) {
        String key = field.getAnnotation(PropertyValue.class).value();
        field.set(instance, resolveProperty(field, key));
      } else if (field.isAnnotationPresent(Prototype.class)) {
        // the field requires the injection to use a prototype.
        field.set(instance, prototype(beanClass));
      } else if (field.getType().isAnnotationPresent(Prototype.class)) {
        // the injected class is annotated as prototype.
        field.set(instance, prototype(beanClass));
      } else {
        // by default, the class is a singleton.
        String requiredName = Optional
          .ofNullable(field.getAnnotation(Named.class))
          .map(Named::value)
          .orElse(null);
        List<BeanRecord> candidates = catalog.fetchCandidates(beanClass, requiredName);
        if (candidates.isEmpty()) {
          log.error("No bean definitions found for class '{}', named '{}'", beanClass.getName(), requiredName);
          throw new CoreException(UNABLE_TO_INJECT_NAMED);
        } else if (candidates.size() == 1) {
          field.set(instance, candidates.get(0).getInstance());
        } else {
          log.error("Multiple bean definitions found, expected 1: {}", candidates);
          throw new CoreException(MULTIPLE_DEFINITIONS);
        }
      }
      field.setAccessible(false);
    } catch (Exception e) {
      log.error("Failed to set instance {} field {}", instance.getClass().getName(), field.getName(), e);
      throw new CoreException();
    }
  }

  /**
   * Resolves a property as required by the provided field from the catalog property sources.
   * @param field the field we are trying to inject in.
   * @param key the property key we are using.
   * @return the value to inject.
   */
  private Object resolveProperty(Field field, String key) {
    String value = catalog.getProperty(key).orElseThrow(() -> new CoreException(PROPERTY_NOT_FOUND));
    String typeName = field.getType().getName();
    switch (typeName) {
      case "java.lang.String":
        return value;
      case "java.lang.Integer":
      case "int":
        return CommonUtils.readInt(value).orElseThrow(() -> new CoreException(PROPERTY_TYPE_ERROR));
      case "java.lang.Boolean":
      case "boolean":
        return CommonUtils.readBool(value).orElseThrow(() -> new CoreException(PROPERTY_TYPE_ERROR));
      case "java.lang.Float":
      case "float":
        return CommonUtils.readFloat(value).orElseThrow(() -> new CoreException(PROPERTY_TYPE_ERROR));
      case "java.lang.Double":
      case "double":
        return CommonUtils.readDouble(value).orElseThrow(() -> new CoreException(PROPERTY_TYPE_ERROR));
      default:
        log.error("Unable to process property type '{}'", typeName);
        throw new CoreException(PROPERTY_TYPE_ERROR);
    }
  }

  /**
   * Looks for the @PostConstruct annotation on any method(s) and invoke it. Note that only one
   * method should be annotated with @PostConstruct, but this method does not care about it.
   *
   * @param instance the object to initialize.
   * @see <a href="https://docs.oracle.com/javaee/7/api/javax/annotation/PostConstruct.html">PostConstruct</a>
   */
  private void init(Object instance) {
    Stream
      .of(instance.getClass().getDeclaredMethods())
      .filter(method -> method.isAnnotationPresent(PostConstruct.class))
      .filter(method -> method.getParameters().length == 0)
      .forEach(method -> {
        boolean canAccess = method.canAccess(instance);
        method.setAccessible(true);
        try {
          method.invoke(instance);
        } catch (Exception e) {
          log.error("Unable to init bean '{}' method '{}'", instance.getClass().getName(), method.getName(), e);
          throw new CoreException(StatusCode.BEAN_INIT_FAILED);
        }
        method.setAccessible(canAccess);
      });
  }

  /**
   * Resolves all properties defined by property sources. Note that @ConfigProvider annotations can add extra
   * property sources after this method has been called.
   * <p></p>
   * Properties resolved by this method are not mandatory by default.
   */
  private void resolveProperties() {
    propertySources.forEach(source -> resolveProperties(source, false));
  }

  /**
   * Resolves a property from a single source.
   * @param source the property source location.
   * @param mandatory indicates if the method should throw an exception when the source is not found.
   */
  private void resolveProperties(String source, boolean mandatory) {
    Optional<Properties> sourceProperties = CommonUtils.readPropertiesFrom(source);
    if (sourceProperties.isPresent()) {
      this.properties.putAll(sourceProperties.get());
      this.properties.stringPropertyNames().forEach(key -> {
        String currentValue = this.properties.getProperty(key);
        this.properties.put(key, System.getProperty(key, currentValue));
      });
    } else if (mandatory) {
      log.error("Mandatory property source '{}' not found", source);
      throw new CoreException(INVALID_PROPERTY_SOURCE);
    }
  }
}
