package es.asgarke.golem.core.constructors;

import es.asgarke.golem.core.annotations.Lazy;
import es.asgarke.golem.core.annotations.Primary;
import es.asgarke.golem.core.annotations.Prototype;
import es.asgarke.golem.core.BeanFactory;
import es.asgarke.golem.core.Inspector;
import es.asgarke.golem.core.definitions.BeanType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

/**
 * Definition to be used when the bean is to be constructed directly by using a regular
 * java constructor.
 * @param <T> the type the instance defines.
 */
@Slf4j
public class ConstructorBeanDefinition<T> extends BeanDefinition<T> {

  private final Constructor<T> constructor;

  private ConstructorBeanDefinition(Class<T> clazz) {
    this.instanceValue = null;
    this.clazz = clazz;
    this.name = Inspector.getBeanName(clazz);
    this.beanType = clazz.isAnnotationPresent(Prototype.class) ? BeanType.Prototype : BeanType.Regular;
    this.constructor = Inspector.resolveConstructor(clazz);
    this.primary = clazz.isAnnotationPresent(Primary.class);
    if (constructor == null) {
      throw new RuntimeException("Unable to find a valid constructor for class " + clazz.getName());
    }
  }

  public static <T> BeanDefinition<T> forClass(Class<T> clazz) {
    return new ConstructorBeanDefinition<>(clazz);
  }

  @Override
  public boolean isLazy() {
    return clazz.isAnnotationPresent(Lazy.class);
  }

  @Override
  public T instance(BeanFactory factory) {
    if (instanceValue == null || beanType == BeanType.Prototype) {
      Object[] args = resolveInitInjections(factory, constructor);
      try {
        instanceValue = constructor.newInstance(args);
        resolveInjections(factory);
        log.info("Instanced bean class {} as {}", clazz.getName(), name);
      } catch (Exception e) {
        String msg = String.format("Failed to instance bean %s with name %s",
          clazz.getName(), name);
        log.error(msg);
        instanceValue = null;
        throw new RuntimeException(msg, e);
      }
    }
    return instanceValue;
  }
}
