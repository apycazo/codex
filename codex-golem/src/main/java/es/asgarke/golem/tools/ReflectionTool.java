package es.asgarke.golem.tools;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class ReflectionTool {

  public static void setFieldValue(Object instance, Field field, Object value) {
    if (field != null) {
      boolean accessLevel = field.canAccess(instance);
      field.setAccessible(true);
      try {
        field.set(instance, value);
      } catch (IllegalAccessException e) {
        String msg = "Unable to set field '" + field.getName() + "' value: '" + value + "'";
        log.warn(msg);
        throw new RuntimeException(msg);
      }
      field.setAccessible(accessLevel);
    }
  }
}
