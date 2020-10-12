package es.asgarke.golem.core;

import es.asgarke.golem.core.annotations.Prototype;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
public class Inspector {

  /**
   * Finds the constructor required to instance a bean from the given class.
   * @param clazz the class to instance.
   * @param <T> the actual class type to use.
   * @return the constructor found, or null on failures.
   */
  public static <T> Constructor<T> resolveConstructor(Class<T> clazz) {
    try {
      Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
        .filter(c -> c.isAnnotationPresent(Inject.class))
        .findFirst()
        .orElseGet(() -> {
          try {
            return clazz.getDeclaredConstructor();
          } catch (Exception e) {
            String msg = "Failed to get default constructor for class " + clazz.getName();
            log.error(msg);
            throw new RuntimeException(msg, e);
          }
        });
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      return clazz.getDeclaredConstructor(parameterTypes);
    } catch (NoSuchMethodException ex) {
      log.warn("No constructor available for building class {}", clazz.getName());
      return null;
    }
  }

  /**
   * Find out if the class provided defines a pure bean or not. A pure bean here
   * is any bean which does not have any dependencies and has a default constructor.
   * <br>
   * Notice that property injections do not make a definition impure.
   * @param clazz the class to check.
   * @return true when the definition belongs to a pure bean.
   */
  public static boolean isPureDefinition(Class<?> clazz) {
    boolean hasConstructorInjection = Arrays
      .stream(clazz.getDeclaredConstructors())
      .anyMatch(c -> c.isAnnotationPresent(Inject.class));
    boolean hasFieldInjection = Arrays
      .stream(clazz.getDeclaredFields())
      .anyMatch(field -> field.isAnnotationPresent(Inject.class));
    boolean hasDeclaredConstructor = true;
    try {
       clazz.getDeclaredConstructor();
    } catch (Exception e) {
      hasDeclaredConstructor = false;
    }
    return !hasConstructorInjection && !hasFieldInjection && hasDeclaredConstructor;
  }

  /**
   * Resolves a bean name. Notice that prototypes can not use names.
   * @param clazz the class to evaluate.
   * @return the name to assign.
   */
  public static String getBeanName(Class<?> clazz) {
    if (clazz.isAnnotationPresent(Named.class) && !clazz.isAnnotationPresent(Prototype.class)) {
      Named named = clazz.getAnnotation(Named.class);
      if (named.value().isBlank()) {
        String beanName = clazz.getName();
        log.info("Detected a bean named with an empty value, reverting to class name: {}", beanName);
        return beanName;
      } else {
        return named.value();
      }
    } else {
      return clazz.getName();
    }
  }

  /**
   * Generates the name that should apply to the method defined bean.
   * @param method the method defining a bean.
   * @return the name assigned to the bean.
   */
  public static String getBeanName(Method method) {
    if (method.isAnnotationPresent(Named.class) && !method.isAnnotationPresent(Prototype.class)) {
      Named named = method.getAnnotation(Named.class);
      if (named.value().isBlank()) {
        String beanName =  method.getName();
        log.info("Detected a bean named with an empty value, reverting to class name: {}", beanName);
        return beanName;
      } else {
        return named.value();
      }
    } else {
      return method.getName();
    }
  }

  /**
   * Checks whether the bean class is a valid definition or not.
   * @param clazz the class to test.
   * @return true is the class is valid, false when some check as failed.
   */
  public static boolean isValidBeanDefinition(Class<?> clazz) {
    boolean result = true;
    if (Modifier.isInterface(clazz.getModifiers())) {
      log.warn("Interfaces are not valid to instance beans");
      result = false;
    }
    if (Modifier.isAbstract(clazz.getModifiers())) {
      log.warn("Abstract classes are not valid to instance beans");
      result = false;
    }
    if (Stream.of(clazz.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).count() > 1) {
      log.warn("Only one constructor method can be annotated with @Inject");
      result = false;
    }
    if (Stream.of(clazz.getDeclaredMethods()).filter(c -> c.isAnnotationPresent(PostConstruct.class)).count() > 1) {
      log.warn("Only one method can be annotated with @PostConstruct");
      result = false;
    }
    return result;
  }
}
