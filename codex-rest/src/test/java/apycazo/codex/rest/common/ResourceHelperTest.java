package apycazo.codex.rest.common;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceHelperTest {

  @Test
  void testResourceFiles() throws IOException {
    String filePath = "classpath:sample.txt";
    File file = ResourceHelper.toFile(filePath).orElse(null);
    assertNotNull(file);
    System.out.println("File path: " + file.getAbsolutePath());
    Resource resource = ResourceHelper.toResource(filePath).orElse(null);
    assertNotNull(resource);
    System.out.println("Resource URI: " + resource.getURI().toString());
    System.out.println("Resource URL: " + resource.getURL().toString());
  }
}