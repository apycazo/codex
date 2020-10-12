package es.asgarke.golem.tools;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class StringTool {

  public static boolean isEmpty(String s) {
    return s == null || s.isBlank();
  }

  public static boolean isNotEmpty(String s) {
    return s != null && !s.isBlank();
  }

  public static Optional<String> readFrom(InputStream inputStream) {
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

  public static String[] splitCommaAndTrim(String s) {
    return splitAndTrim(s, ",");
  }

  public static String[] splitAndTrim(String s, String separator) {
    if (isEmpty(s)) return new String[0];
    else return s.trim().split("\\s*" + separator + "\\s*");
  }

  public static String joinPaths(String ... segments) {
    Function<String, String> normalize = segment -> {
      String path = segment.strip();
      return String.format("%s%s", path.startsWith("/") || path.startsWith("http://") ? "" : "/",
        path.endsWith("/") ? path.substring(0, path.length() - 1) : path);
    };
    if (segments == null || segments.length == 0) {
      return "";
    } else {
      return Arrays.stream(segments)
        .filter(Objects::nonNull)
        .filter(v -> !v.isBlank())
        .map(normalize)
        .collect(Collectors.joining());
    }
  }
}
