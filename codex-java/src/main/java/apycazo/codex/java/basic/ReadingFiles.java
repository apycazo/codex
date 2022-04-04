package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    URI uri = ReadingFiles.class.getClassLoader().getResource(filename).toURI();
    Path path = Paths.get(uri);
    String content = new String(Files.readAllBytes(path));
    log.info("Content: {}", content);
    if (!"playground-snippets".equals(content)) {
      throw new Exception("Failed test");
    }
  }
}
