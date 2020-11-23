package apycazo.codex.rest.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Optional;

public class ResourceHelper {

  public static Optional<Resource> toResource(String path) {
    if (StringUtils.isEmpty(path)) {
      return Optional.empty();
    } else {
      Resource resource;
      if (path.startsWith("classpath:")) {
        String actualPath = path.substring("classpath:".length());
        resource = new ClassPathResource(actualPath);
      } else if (path.startsWith("file:")) {
        String actualPath = path.substring("file:".length());
        resource = new FileSystemResource(actualPath);
      } else {
        resource = new FileSystemResource(path);
      }
      if (resource.exists() && resource.isFile()) {
        return Optional.of(resource);
      } else {
        return Optional.empty();
      }
    }
  }

  public static Optional<File> toFile(String path) {
    Optional<Resource> resource = toResource(path);
    if (resource.isEmpty()) {
      return Optional.empty();
    } else {
      Resource resourceValue = resource.get();
      try {
        return Optional.of(resourceValue.getFile());
      } catch (Exception e) {
        return Optional.empty();
      }
    }
  }
}

