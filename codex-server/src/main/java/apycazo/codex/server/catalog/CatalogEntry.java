package apycazo.codex.server.catalog;

import apycazo.codex.server.ServerUtil;
import apycazo.codex.server.errors.CoreException;
import apycazo.codex.server.errors.StatusCode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class CatalogEntry {

  private final Object instance;
  private final Set<String> references;
  private final boolean requiredInit;

  public CatalogEntry(Object instance) {
    this.instance = instance;
    this.references = new HashSet<>();
    this.requiredInit = hasMethodAnnotatedWith(PostConstruct.class);
  }

  public CatalogEntry(Object instance, String requiredBy) {
    this(instance);
    if (!ServerUtil.isEmptyOrBlank(requiredBy)) {
      references.add(requiredBy);
    }
  }

  private Stream<Method> findMethodsAnnotatedWith(Class<? extends Annotation> annotation) {
    return Stream.of(instance.getClass().getDeclaredMethods())
      .filter(method -> method.isAnnotationPresent(annotation));
  }

  public boolean hasMethodAnnotatedWith(Class<? extends Annotation> annotation) {
    return Stream.of(instance.getClass().getDeclaredMethods())
      .anyMatch(method -> method.isAnnotationPresent(annotation));
  }

  public Object getInstance() {
    return instance;
  }

  public int getReferenceCount() {
    return references.size();
  }

  public void addReference(String beanName) {
    references.add(beanName);
  }

  public boolean isRequiredInit() {
    return requiredInit;
  }

  public Class<?> getInstanceClass() {
    return instance.getClass();
  }

  /**
   * Initializes the bean, if required
   */
  public void initialize() {
    if (requiredInit) {
      findMethodsAnnotatedWith(PostConstruct.class)
        .filter(method -> method.getParameters().length == 0)
        .forEach(method -> {
          boolean canAccess = method.canAccess(instance);
          method.setAccessible(true);
          try {
            method.invoke(instance);
          } catch (Exception e) {
            log.error("Unable to init bean '{}' method '{}'", instance.getClass().getName(), method.getName());
            throw new CoreException(StatusCode.BEAN_INIT_FAILED);
          }
          method.setAccessible(canAccess); // is this really needed?
        });
    }
  }
}
