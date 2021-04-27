package es.asgarke.golem.tools;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class FileTool {

  private static final String FILE_HDR = "file:";
  private static final String CLASSPATH_HDR = "classpath:";

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
        Optional<File> fileOption = resolveFileFromStringPath(FILE_HDR + filePath);
        if (fileOption.isPresent()) {
          return fileOption;
        } else {
          return resolveFileFromStringPath(CLASSPATH_HDR + filePath);
        }
      }
      if (file != null && file.exists()) {
        return Optional.of(file);
      } else {
        return Optional.empty();
      }
    } catch (Exception e) {
      log.warn("Unable to resolve file path '{}'", filePath, e);
      return Optional.empty();
    }
  }

}
