package apycazo.codex.minion.context.conditions;

import apycazo.codex.minion.common.CommonUtils;
import apycazo.codex.minion.context.catalog.Catalog;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Resolver if a bean must be instanced or not taking a look into the existing annotations.
 * Currently annotations are evaluated independently, but only the first one is considered.
 */
public class ConditionResolver {

  private static final List<Class<? extends Annotation>> annotations = Stream
    .of(OnPropertyCondition.class)
    .collect(Collectors.toList());

  public static List<Class<?>> filterByCondition(Catalog catalog, List<Class <?>> classList) {
    if (classList == null || classList.isEmpty() || catalog == null) {
      return classList;
    } else {
      return classList.stream().filter(clazz -> matchesConditions(catalog, clazz)).collect(Collectors.toList());
    }
  }

  public static boolean matchesConditions(Catalog catalog, AnnotatedElement annotatedElement) {
    if (!CommonUtils.isAnnotationPresent(annotatedElement, annotations)) {
      return true; // no condition
    } else if (annotatedElement.isAnnotationPresent(OnPropertyCondition.class)) {
      OnPropertyCondition condition = annotatedElement.getAnnotation(OnPropertyCondition.class);
      String key = condition.value();
      String matching = condition.value();
      Optional<String> property = catalog.getProperty(key);
      if (property.isEmpty()) {
        return condition.matchOnMissing();
      } else {
        return matching.isEmpty() || property.get().equals(matching);
      }
    } else if (annotatedElement.isAnnotationPresent(OnClassCondition.class)) {
      OnClassCondition condition = annotatedElement.getAnnotation(OnClassCondition.class);
      try {
        Class.forName(condition.className());
        return true;
      } catch (ClassNotFoundException e) {
        return false;
      }
    } else {
      return false; // should never exit through here.
    }
  }
}
