package apycazo.codex.rest.common;

import apycazo.codex.rest.common.util.ResourceHelper;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(JUnitPlatform.class)
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