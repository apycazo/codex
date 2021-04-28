package es.asgarke.golem.core;

import es.asgarke.golem.tools.ParserTool;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class BeanProperties {

  private final Properties properties;
  private final Set<String> paths;

  public BeanProperties() {
    properties = new Properties();
    paths = new LinkedHashSet<>();
  }

  public BeanProperties mergePropertiesFrom(String path, boolean isMandatory) {
    Optional<Properties> readProps = ParserTool.readFromStringPath(path);
    if (readProps.isEmpty() && isMandatory) {
      String msg = "Failed to find & parse required properties location: " + path;
      log.error(msg);
      throw new RuntimeException(msg);
    } else {
      readProps.ifPresent(props -> {
        properties.putAll(props);
        paths.add(path);
      });
    }
    return this;
  }

  public Stream<String> getPathsStream() {
    return paths.stream();
  }

  public Stream<String> getPropertyKeyStream() {
    return properties.stringPropertyNames().stream();
  }

  public String resolvePropertyTemplate(String template) {
    if (template == null || template.isBlank()) {
      throw new RuntimeException("Property template is empty");
    } else {
      String[] elements = template.strip().split(":");
      String key = elements[0];
      if (properties.containsKey(key)) {
        return properties.getProperty(key);
      } else if (elements.length > 1) {
        return elements[1];
      } else if (template.endsWith(":")) {
        return "";
      } else {
        throw new RuntimeException("Property " + key + " not found");
      }
    }
  }

  public Object resolveProperty(Class<?> clazz, String value) {
    String typeName = clazz.getName();
    return resolveProperty(typeName, value);
  }

  public Object resolveProperty(Field field, String value) {
    String typeName = field.getType().getName();
    return resolveProperty(typeName, value);
  }

  /**
   * Resolves a property as required by the provided field from the catalog property sources.
   * @param typeName the type (class) name we are trying to inject in.
   * @param value the property value we are resolving.
   * @return the value to inject.
   */
  public Object resolveProperty(String typeName, String value) {
    String typeErrorMsg = "Property type " + typeName + " can not be parsed";
    switch (typeName) {
      case "java.lang.String":
        return value;
      case "java.lang.Integer":
      case "int":
        return ParserTool.readInt(value).orElseThrow(() -> new RuntimeException(typeErrorMsg));
      case "java.lang.Boolean":
      case "boolean":
        return ParserTool.readBool(value).orElseThrow(() -> new RuntimeException(typeErrorMsg));
      case "java.lang.Float":
      case "float":
        return ParserTool.readFloat(value).orElseThrow(() -> new RuntimeException(typeErrorMsg));
      case "java.lang.Double":
      case "double":
        return ParserTool.readDouble(value).orElseThrow(() -> new RuntimeException(typeErrorMsg));
      default:
        log.error("Unable to process property type '{}'", typeName);
        throw new RuntimeException(typeErrorMsg);
    }
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  public Optional<String> getStringProperty(String key) {
    return Optional.ofNullable(properties.getProperty(key));
  }

  public Optional<Integer> getIntProperty(String key) {
    return ParserTool.readInt(properties.getProperty(key));
  }

  public Optional<Boolean> getBooleanProperty(String key) {
    return ParserTool.readBool(properties.getProperty(key));
  }
}
