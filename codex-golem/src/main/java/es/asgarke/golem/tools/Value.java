package es.asgarke.golem.tools;

import java.util.Collection;
import java.util.function.Consumer;

public class Value {

  private Value() {
    // empty
  }

  public static <T> T orDefault(T value, T defaultValue) {
    return isEmptyOrNull(value) ? defaultValue : value;
  }

  public static <T> void ifNotEmptyOrNull(T[] value, Consumer<T[]> consumer) {
    if (!isEmptyOrNull(value)) {
      consumer.accept(value);
    }
  }

  public static <T> void ifNotEmptyOrNull(Collection<T> value, Consumer<Collection<T>> consumer) {
    if (!isEmptyOrNull(value)) {
      consumer.accept(value);
    }
  }

  public static <T> void ifNotEmptyOrNull(T value, Consumer<T> consumer) {
    if (!isEmptyOrNull(value)) {
      consumer.accept(value);
    }
  }

  public static boolean isEmptyOrNull(Object object) {
    if (object == null) {
      return true;
    } else if (object instanceof String) {
      String string = (String) object;
      return string.trim().isEmpty();
    } else if (object instanceof Object[]) {
      Object[] array = (Object[]) object;
      return array.length == 0;
    } else if (object instanceof Collection) {
      Collection<?> collection = (Collection<?>) object;
      return collection.isEmpty();
    } else if (object instanceof Iterable) {
      Iterable<?> iterable = (Iterable<?>) object;
      return iterable.iterator().hasNext();
    } else {
      return false;
    }
  }
}
