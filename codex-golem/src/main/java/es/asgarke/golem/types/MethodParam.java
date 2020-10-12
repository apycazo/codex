package es.asgarke.golem.types;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class MethodParam {

  private final Class<?> type;
  private final Set<Annotation> annotations;

  public MethodParam(Class<?> type, Annotation[] annotations) {
    this.type = type;
    // java will probably give us proxies here, not the actual classes!
    this.annotations = Stream.of(annotations).collect(Collectors.toSet());
  }

  public <T> Optional<T> getAnnotation(Class<T> annotationClass) {
    return annotations.stream()
      .filter(annotation -> annotationClass.isAssignableFrom(annotation.getClass()))
      .findFirst()
      .map(annotationClass::cast);
  }

  public Class<?> getType() {
    return type;
  }

  public static MethodParam[] params(Method method) {
    MethodParam[] params = new MethodParam[method.getParameterCount()];
    Class<?>[] parameterTypes = method.getParameterTypes();
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    for (int i = 0; i < method.getParameterCount(); i++) {
      params[i] = new MethodParam(parameterTypes[i], parameterAnnotations[i]);
    }
    return params;
  }
}
