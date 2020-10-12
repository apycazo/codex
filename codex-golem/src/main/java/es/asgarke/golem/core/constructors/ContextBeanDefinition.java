package es.asgarke.golem.core.constructors;

import es.asgarke.golem.core.BeanFactory;
import es.asgarke.golem.core.GolemContext;
import es.asgarke.golem.core.definitions.BeanType;

/**
 * A bean definition wrapper, just so the context can be injected as a regular bean.
 */
public class ContextBeanDefinition extends BeanDefinition<GolemContext> {

  private final GolemContext context;

  public ContextBeanDefinition(GolemContext context) {
    this.context = context;
    this.clazz = GolemContext.class;
  }

  @Override
  public GolemContext instance(BeanFactory factory) {
    return context;
  }

  @Override
  public boolean isLazy() {
    return false;
  }

  @Override
  public boolean isPrimary() {
    return true;
  }

  @Override
  public BeanType getBeanType() {
    return BeanType.Regular;
  }

  @Override
  public String getName() {
    return "GolemContext";
  }

  @Override
  public void resolveInjections(BeanFactory factory) {

  }

  @Override
  public void setBeanType(BeanType beanType) {

  }
}
