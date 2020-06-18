package apycazo.codex.minion.context;

import apycazo.codex.minion.common.CoreException;

import static apycazo.codex.minion.common.StatusCode.BEAN_CAST_FAILED;

public class BeanRecord {

  private final Object instance;
  private final String name;

  public static BeanRecord of(Object instance, String name) {
    return new BeanRecord(instance, name);
  }

  private BeanRecord(Object instance, String name) {
    this.instance = instance;
    this.name = name;
  }

  public boolean classMatches(Class<?> clazz) {
    return instance.getClass().getName().equals(clazz.getName());
  }

  public boolean classAssignableTo(Class<?> clazz) {
    return clazz.isAssignableFrom(instance.getClass());
  }

  public boolean hasName(String name) {
    return this.name.equals(name);
  }

  public Object getInstance() {
    return instance;
  }

  public <T> T getInstanceAs(Class<? extends T> clazz) {
    if (classAssignableTo(clazz)) {
      return clazz.cast(instance);
    } else {
      throw new CoreException(BEAN_CAST_FAILED);
    }
  }

  @Override
  public String toString() {
    return String.format("%s:%s", instance.getClass().getName(), name);
  }

}
