package es.asgarke.golem.http;

import es.asgarke.golem.http.types.Response;
import es.asgarke.golem.tools.FileTool;
import es.asgarke.golem.tools.StringTool;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class StaticResolver {

  private final Set<String> basePaths;

  public StaticResolver() {
    basePaths = new HashSet<>();
  }

  public void addClassPathLocation(String location) {
    register(FileTool.CLASSPATH_HDR, location);
  }

  public void addClassPathLocations(String[] locations) {
    if (locations != null && locations.length > 0) {
      for (String location : locations) {
        addClassPathLocation(location);
      }
    }
  }

  public void addFilePathLocation(String location) {
    register(FileTool.FILE_HDR, location);
  }

  public void addFilePathLocations(String[] locations) {
    if (locations != null && locations.length > 0) {
      for (String location : locations) {
        addFilePathLocation(location);
      }
    }
  }

  public Response resolveResource(String location) {
    if (basePaths.isEmpty()) {
      return Response.notFound();
    }
    // try to resolve from file mime type:
    String normalizedLocation = normalizeLocation(location);
    Optional<File> file = basePaths.stream()
      .map(base -> base + normalizedLocation)
      .map(FileTool::resolveFileFromStringPath)
      .filter(Optional::isPresent)
      .findFirst()
      .map(Optional::get);
    if (file.isPresent()) {
      try {
        Path path = file.get().toPath();
        String mediaType = Files.probeContentType(path);
        String content = new String(Files.readAllBytes(path));
        return Response.ok(content).withMediaType(mediaType);
      } catch (IOException e) {
        log.error("Unable to resolve file type for '{}'", file.get().getAbsolutePath());
        return Response.notFound();
      }
    } else {
      return Response.notFound();
    }
  }

  private void register(String header, String path) {
    String normalizedPath = normalizePath(path);
    if (!StringTool.isEmpty(normalizedPath)) {
      String location = header + normalizedPath;
      basePaths.add(location);
      log.info("Added static content base location '{}'", location);
    }
  }

  private String normalizePath(String path) {
    if (StringTool.isEmpty(path)) {
      return null;
    }
    String fileSeparator = System.getProperty("file.separator");
    if (!path.endsWith(fileSeparator)) {
      return path + fileSeparator;
    } else {
      return path;
    }
  }

  private String normalizeLocation(String location) {
    String locationSeparator = "/";
    if (location.startsWith(locationSeparator)) {
      return location.substring(1);
    } else {
      return location;
    }
  }
}
