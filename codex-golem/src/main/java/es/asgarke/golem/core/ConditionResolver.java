package es.asgarke.golem.core;

import es.asgarke.golem.core.annotations.ConditionalOnProperty;
import es.asgarke.golem.core.constructors.BeanDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.AnnotatedElement;

@Slf4j
@RequiredArgsConstructor
public class ConditionResolver {

  private final BeanProperties properties;

  public boolean definitionMatchesConditions(BeanDefinition<?> definition) {
    return definitionMatchesConditions(definition, null);
  }

  public boolean definitionMatchesConditions(BeanDefinition<?> definition, AnnotatedElement element) {
    if (definition == null) {
      return false;
    } else {
      return propertyConditionMatches(definition.getClazz()) && propertyConditionMatches(element);
    }
  }

  private boolean propertyConditionMatches(AnnotatedElement element) {
    if (element == null || !element.isAnnotationPresent(ConditionalOnProperty.class)) {
      return true;
    } else {
      ConditionalOnProperty[] conditions = element.getAnnotationsByType(ConditionalOnProperty.class);
      boolean isOk = true;
      for (ConditionalOnProperty condition : conditions) {
        String key = condition.value();
        String expected = condition.expectedValue();
        boolean matchIfMissing = condition.matchIfMissing();
        if (properties.getProperty(key, "").isBlank()) {
          isOk &= matchIfMissing;
        } else if (!expected.isEmpty()){
          isOk &= expected.equals(properties.getProperty(key));
        }
      }
      return isOk;
    }
  }

}
