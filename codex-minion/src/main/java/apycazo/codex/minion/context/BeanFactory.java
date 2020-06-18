package apycazo.codex.minion.context;

import apycazo.codex.minion.common.CommonUtils;
import apycazo.codex.minion.common.CoreException;
import apycazo.codex.minion.common.StatusCode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static apycazo.codex.minion.common.StatusCode.*;

/**
 * Creates new instances as required and injects dependencies. To resolve:
 * <p>
 *    1. class C extends B, class B extends A: When I need to inject by class A I have 3 possible candidates?
 *    2. Classes A and B implement I (interface), ¿how to resolve which one to inject? -> by name!, how to instance?
 * </p>
 * <p>
 *   Class A implements I
 *   Class B extends A
 *   Class C depends on I
 * </p>
 */
@Slf4j
public class BeanFactory {

  private final Catalog catalog;

  public BeanFactory(Catalog catalog) {
    this.catalog = catalog;
  }

  public void singletons(List<Class<?>> classList) {
    // --- create instances
    for (Class<?> clazz : classList) {
      Object instance = instance(clazz);
      String name = Optional // resolve the instance name, defaulting to the class name
        .ofNullable(clazz.getAnnotation(Named.class))
        .map(Named::value).orElse(clazz.getName());
      catalog.register(instance, name);
    }
    // --- resolve dependencies
    catalog.records().forEach(record -> {
      Object instance = record.getInstance();
      Stream.of(instance.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(PropertyValue.class))
        .forEach(field -> injectDependency(instance, field));
    });
    // --- init beans
    catalog.records().map(BeanRecord::getInstance).forEach(this::init);
  }

  public <T> T prototype(Class<? extends T> clazz) {
    T instance = instance(clazz);
    Stream.of(instance.getClass().getDeclaredFields())
      .filter(field -> field.isAnnotationPresent(Inject.class))
      .forEach(field -> injectDependency(instance, field));
    return instance;
  }

  public <T> T instance(Class<? extends T> clazz) {
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
      default:
        log.error("Unable to process property type '{}'", typeName);
        throw new CoreException(PROPERTY_TYPE_ERROR);
    }
  }

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
}
