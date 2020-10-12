package es.asgarke.golem.core.constructors;

import es.asgarke.golem.core.BeanFactory;
import es.asgarke.golem.core.annotations.NonRequired;
import es.asgarke.golem.core.annotations.PropertyValue;
import es.asgarke.golem.core.definitions.BeanType;
import es.asgarke.golem.tools.ReflectionTool;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public abstract class BeanDefinition<T> {

  @Getter
  protected Class<T> clazz;
  protected T instanceValue;
  @Getter
  protected String name;
  @Getter @Setter
  protected BeanType beanType;
  @Getter
  protected boolean primary;

  public abstract T instance(BeanFactory factory);

  public abstract boolean isLazy();

  public boolean canBeCastTo(Class<?> target) {
    return target.isAssignableFrom(clazz);
  }

  public void resolveInjections(BeanFactory factory) {
    // resolve property injections
    Arrays.stream(clazz.getDeclaredFields())
      .filter(field -> field.isAnnotationPresent(PropertyValue.class))
      .forEach(field -> {
        String propertyTemplate = field.getAnnotation(PropertyValue.class).value();
        String stringValue = factory.getProperties().resolvePropertyTemplate(propertyTemplate);
        Object value = factory.getProperties().resolveProperty(field, stringValue);
        ReflectionTool.setFieldValue(instanceValue, field, value);
      });
    // resolve bean injections
    Arrays.stream(clazz.getDeclaredFields())
      .filter(field -> field.isAnnotationPresent(Inject.class))
      .forEach(field -> {
        String name = Optional.ofNullable(field.getAnnotation(Named.class))
          .map(Named::value)
          .orElse("");
        Optional<?> bean = factory.resolveBean(field.getType(), name);
        if (bean.isPresent()) {
          ReflectionTool.setFieldValue(instanceValue, field, bean.get());
        } else if (!field.isAnnotationPresent(NonRequired.class)) {
          String msg = "Unable to resolve required bean dependency class " + field.getType().getName() +
            " named '" + name + "'";
          log.warn(msg);
          throw new RuntimeException(msg);
        }
      });
    handlePostConstruct();
  }

  protected Object[] resolveInitInjections(BeanFactory factory, Executable executable) {
    Class<?>[] parameterTypes = executable.getParameterTypes();
    Annotation[][] parameterAnnotations = executable.getParameterAnnotations();
    Object[] args = new Object[parameterTypes.length];
    for (int i = 0; i < args.length; i++) {
      String name = Arrays.stream(parameterAnnotations[i])
        .filter(a -> a.annotationType() == Named.class)
        .map(a -> ((Named) a).value())
        .findFirst().orElse("");
      Class<?> requiredClazz = parameterTypes[i];
      PropertyValue propertyValue = Arrays.stream(parameterAnnotations[i])
        .filter(a -> a.annotationType() == PropertyValue.class)
        .map(v -> (PropertyValue) v)
        .findFirst().orElse(null);
      if (propertyValue != null) {
        String stringValue = factory.getProperties().resolvePropertyTemplate(propertyValue.value());
        Object value = factory.getProperties().resolveProperty(requiredClazz, stringValue);
        args[i] = value;
      } else {
        Object bean = factory
          .resolveBean(requiredClazz, name)
          .orElse(null);
        if (bean == null) {
          if (Arrays.stream(parameterAnnotations[i]).anyMatch(a -> a.annotationType() == PropertyValue.class)) {
            args[i] = null;
          } else {
            throw new RuntimeException("Failed to find bean with name '" + name + "' and class " + requiredClazz);
          }
        } else {
          args[i] = bean;
        }
      }
    }
    return args;
  }

  protected void handlePostConstruct() {
    Arrays.stream(clazz.getDeclaredMethods())
      .filter(method -> method.isAnnotationPresent(PostConstruct.class))
      .findFirst().ifPresent(method -> {
        try {
          boolean canAccess = method.canAccess(instanceValue);
          method.setAccessible(true);
          method.invoke(instanceValue);
          method.setAccessible(canAccess);
        } catch (Exception e) {
          String msg = "Failed to invoke post-construct method";
          log.error(msg);
          throw new RuntimeException(msg, e);
        }
    });
  }

  public String fullName() {
    return clazz.getName() + ":" + name;
  }

  @Override
  public String toString() {
    String format = "Name: %s, Type: %s, Lazy? %b, Prototype? %b";
    return String.format(format, name, clazz.getName(), isLazy(), beanType == BeanType.Prototype);
  }
}
