package es.asgarke.golem.core.constructors;

import es.asgarke.golem.core.BeanFactory;
import es.asgarke.golem.core.Inspector;
import es.asgarke.golem.core.annotations.Lazy;
import es.asgarke.golem.core.annotations.Primary;
import es.asgarke.golem.core.annotations.Prototype;
import es.asgarke.golem.core.definitions.BeanType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Definitions for beans that are not using a constructor directly, and instead delegate
 * into another class method (for example, to be used on @ConfigurationBean classes).
 */
@Slf4j
public class MethodBeanDefinition<T> extends BeanDefinition<T> {

  private final Object base;
  private final Method method;

  public static <T> BeanDefinition<T> forClassMethod(Class<T> clazz, Method method, Object base) {
    return new MethodBeanDefinition<>(clazz, method, base);
  }

  private MethodBeanDefinition(Class<T> clazz, Method method, Object base) {
    this.instanceValue = null;
    this.clazz = clazz;
    this.method = method;
    this.base = base;
    this.name = Inspector.getBeanName(method);
    this.primary = method.isAnnotationPresent(Primary.class);
    this.beanType = method.isAnnotationPresent(Prototype.class) ? BeanType.Prototype : BeanType.Regular;
  }

  @Override
  public boolean isLazy() {
    return method.isAnnotationPresent(Lazy.class);
  }

  @Override
  public T instance(BeanFactory factory) {
    if (instanceValue == null || beanType == BeanType.Prototype) {
      Object[] args = resolveInitInjections(factory, method);
      try {
        instanceValue = clazz.cast(method.invoke(base, args));
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
