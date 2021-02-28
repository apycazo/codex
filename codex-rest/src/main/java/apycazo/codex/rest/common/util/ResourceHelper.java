package apycazo.codex.rest.common.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Optional;

@Slf4j
public class ResourceHelper {

  @SneakyThrows
  public static Optional<Resource> toResource(String path) {
    if (!StringUtils.hasLength(path)) {
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
        log.warn("Resource {} is not a file or does not exist", resource.getFile().getPath());
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
        log.warn("toFile with path '{}' failed", path, e);
        return Optional.empty();
      }
    }
  }
}

