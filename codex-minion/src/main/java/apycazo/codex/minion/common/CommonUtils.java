package apycazo.codex.minion.common;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

@Slf4j
public final class CommonUtils {

  private static final String FILE_HDR = "file:", CLASSPATH_HDR = "classpath:";

  public static Optional<String> readFromInputStream(InputStream inputStream) {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) resultStringBuilder.append(line).append("\n");
    } catch (Exception e) {
      log.warn("Unable to read file from inputStream", e);
      return Optional.empty();
    }
    return Optional.of(resultStringBuilder.toString());
  }

  public static Optional<File> resolveFileFromStringPath(String filePath) {
    try {
      File file;
      if (filePath.startsWith(CLASSPATH_HDR)) {
        filePath = filePath.substring(CLASSPATH_HDR.length());
        URL url = ClassLoader.getSystemResource(filePath);
        file = url != null ? new File(url.toURI()) : null;
      } else if (filePath.startsWith(FILE_HDR)) {
        filePath = filePath.substring(FILE_HDR.length());
        file = new File(filePath);
      } else {
        file = new File(filePath);
      }
      if (file != null && file.exists()) {
        return Optional.of(file);
      } else {
        log.warn("File with path '{}' does not seem to exist", filePath);
        return Optional.empty();
      }
    } catch (Exception e) {
      log.warn("Unable to resolve file path '{}'", filePath, e);
      return Optional.empty();
    }
  }

  public static Optional<String> readStringFrom(File file) {
    try (InputStream stream = new FileInputStream(file)) {
      return readFromInputStream(stream);
    } catch (Exception e) {
      log.warn("Failed to read from file");
      return Optional.empty();
    }
  }

  public static Optional<String> readStringFrom(String filePath) {
    Optional<File> file = resolveFileFromStringPath(filePath);
    return file.isPresent() ? readStringFrom(file.get()) : Optional.empty();
  }

  public static Optional<Properties> readPropertiesFrom(File file) {
    Properties properties = new Properties();
    try (InputStream stream = new FileInputStream(file)) {
      properties.load(stream);
      return Optional.of(properties);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public static Optional<Properties> readPropertiesFrom(String filePath) {
    if (filePath == null || filePath.trim().isEmpty()) {
      return Optional.of(new Properties());
    } else {
      Optional<File> file = resolveFileFromStringPath(filePath);
      return file.isPresent() ? readPropertiesFrom(file.get()) : Optional.empty();
    }
  }

  public static Optional<Integer> readInt(Object obj) {
    if (obj == null || !obj.getClass().isAssignableFrom(String.class)) {
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
    if (obj == null || !obj.getClass().isAssignableFrom(String.class)) {
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
    if (obj == null || !obj.getClass().isAssignableFrom(Float.class)) {
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
    if (obj == null || !obj.getClass().isAssignableFrom(Float.class)) {
      return Optional.empty();
    } else {
      try {
        return Optional.of(Double.parseDouble((String) obj));
      } catch (Exception e) {
        log.warn("Unable to cast as float value: {}", obj, e);
        return Optional.empty();
      }
    }
  }

  public static boolean isEmptyOrBlank(String string) {
    return string == null || string.trim().isEmpty();
  }

  public static boolean isAnnotationPresent(Field field, List<Class<? extends Annotation>> annotations) {
    if (annotations == null) {
      return false;
    } else {
      return annotations.stream().anyMatch(field::isAnnotationPresent);
    }
  }
}
