package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reads a file from classpath using the classloader to read the file as a resource, and converting it into a
 * String using the nio API.
 * This snippet requires the file 'src/main/resources/projectName.txt'.
 */
@Slf4j
public class ReadingFiles {

  public static void main(String[] args) throws Exception {
    String filename = "projectName.txt";
    log.info("Loading file: {}", filename);
    URL url = ReadingFiles.class.getClassLoader().getResource(filename);
    Path path = Paths.get(url.toURI());
    String content = new String(Files.readAllBytes(path));
    log.info("Content: {}", content);
    assertThat(content).isEqualTo("playground-snippets");
  }

  private static Optional<Properties> readProperties(String path) {
    URL resource = ReadingFiles.class.getClassLoader().getResource(path);
    File file = resource != null ? new File(resource.getFile()) : new File(path);
    if (!file.exists()) {
      log.warn("File '{}' does not exist", path);
    } else if (!file.isFile()) {
      log.warn("File '{}' is not a file", path);
    } else if (!file.canRead()) {
      log.warn("File '{}' can not be read", path);
    } else {
      try (InputStream stream = new FileInputStream(file)) {
        Properties properties = new Properties();
        properties.load(stream);
        return Optional.of(properties);
      } catch (Exception e) {
        log.error("Failed to create input stream from '{}'", path);
      }
    }
    return Optional.empty();
  }
}
