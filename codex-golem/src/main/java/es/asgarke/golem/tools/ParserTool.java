package es.asgarke.golem.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class ParserTool {

  public static Optional<Properties> readPropertiesFrom(File file) {
    Properties properties = new Properties();
    try (InputStream stream = new FileInputStream(file)) {
      properties.load(stream);
      return Optional.of(properties);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<Properties> readFromStringPath(String path) {
    String classpathHeader = "classpath:";
    String fileHeader = "file:";
    Properties properties = new Properties();
    try {
      if (path.startsWith(classpathHeader)) {
        String actualPath = path.substring(classpathHeader.length());
        URL resource = ParserTool.class.getClassLoader().getResource(actualPath);
        if (resource != null) {
          Path filePath = Paths.get(resource.toURI());
          readPropertiesFrom(filePath.toFile()).ifPresent(properties::putAll);
        }
      } else {
        String actualPath = path.startsWith(fileHeader) ? path.substring(fileHeader.length()) : path;
        File file = new File(actualPath);
        return readPropertiesFrom(file);
      }
      return Optional.of(properties);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<Integer> readInt(Object obj) {
    if (obj instanceof Integer) {
      return Optional.of((Integer) obj);
    } else if (!(obj instanceof String)) {
      return Optional.empty();
    } else {
      try {
        return Optional.of(Integer.parseInt((String) obj));
      } catch (Exception e) {
        log.warn("Unable to cast as int value: {}", obj, e);
        return Optional.empty();
      }
    }
  }

  public static Optional<Boolean> readBool(Object obj) {
    if (obj instanceof Boolean) {
      return Optional.of((boolean) obj);
    } else if (!(obj instanceof String)) {
      return Optional.empty();
    } else {
      try {
        return Optional.of(Boolean.parseBoolean((String) obj));
      } catch (Exception e) {
        log.warn("Unable to cast as boolean value: {}", obj, e);
        return Optional.empty();
      }
    }
  }

  public static Optional<Float> readFloat(Object obj) {
    if (obj instanceof Float) {
      return Optional.of((Float) obj);
    } else if (!(obj instanceof String)) {
      return Optional.empty();
    } else {
      try {
        return Optional.of(Float.parseFloat((String) obj));
      } catch (Exception e) {
        log.warn("Unable to cast as float value: {}", obj, e);
        return Optional.empty();
      }
    }
  }

  public static Optional<Double> readDouble(Object obj) {
    if (obj instanceof Double) {
      return Optional.of((Double) obj);
    } else if (!(obj instanceof String)) {
      return Optional.empty();
    } else {
      try {
        return Optional.of(Double.parseDouble((String) obj));
      } catch (Exception e) {
        log.warn("Unable to cast as int value: {}", obj, e);
        return Optional.empty();
      }
    }
  }

  public static <T> T parse(String value, Class<T> clazz) {
    return parse(value, clazz, null);
  }

  public static <T> T parse(String value, Class<T> clazz, ObjectMapper mapper) {
    if (value == null || value.isBlank()) {
      return null;
    } else {
      String typeName = clazz.getName();
      switch (typeName) {
        case "java.lang.String":
          return clazz.cast(value);
        case "java.lang.Integer":
        case "int":
          return readInt(value)
            .map(clazz::cast)
            .orElseThrow(() -> new RuntimeException("Failed to parse int from " + value));
        case "java.lang.Boolean":
        case "boolean":
          return readBool(value)
            .map(clazz::cast)
            .orElseThrow(() -> new RuntimeException("Failed to parse boolean from " + value));
        case "java.lang.Float":
        case "float":
          return readFloat(value)
            .map(clazz::cast)
            .orElseThrow(() -> new RuntimeException("Failed to parse boolean from " + value));
        case "java.lang.Double":
        case "double":
          return readDouble(value)
            .map(clazz::cast)
            .orElseThrow(() -> new RuntimeException("Failed to parse boolean from " + value));
        default:
          if (mapper == null) {
            throw new RuntimeException("Failed to parse to class " + clazz.getName() + " (no object mapper found)");
          } else {
            try {
              return clazz.cast(mapper.readValue(value, clazz));
            } catch (IOException e) {
              throw new RuntimeException("Failed to parse to class " + clazz.getName(), e);
            }
          }
      }
    }
  }
}
