package apycazo.codex.server;

import apycazo.codex.server.catalog.Catalog;
import apycazo.codex.server.errors.CoreException;
import apycazo.codex.server.errors.StatusCode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.stream.Stream;

@Slf4j
public class AppContext {

  private final Catalog catalog;

  private AppContext() {
    catalog = new Catalog(this);
  }

  public static AppContext run(Class<?> initClass) {
    return run(initClass, new String[0]);
  }

  public static AppContext run(Class<?> initClass, String[] args) {
    if (initClass == null) {
      log.error("Init class must not be null");
      throw new CoreException(StatusCode.INVALID_CONFIGURATION);
    } else {
      return run(initClass.getPackageName(), args);
    }
  }

  public static AppContext run(String basePackageToScan) {
    return run(basePackageToScan, new String[0]);
  }

  public static AppContext run(String basePackageToScan, String[] args) {
    if (ServerUtil.isEmptyOrBlank(basePackageToScan)) {
      log.error("Base package must not be null nor blank");
      throw new CoreException(StatusCode.INVALID_CONFIGURATION);
    } else {
      return initApplication(basePackageToScan, args);
    }
  }

  private static AppContext initApplication(String basePackageToScan, String[] args) {
    AppContext context = new AppContext();
    try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(basePackageToScan).scan()) {
      // find singletons
      ClassInfoList singletons = scanResult.getClassesWithAnnotation(Singleton.class.getName());
      singletons.forEach(classInfo -> {
        Class<?> clazz;
        try {
           clazz = Class.forName(classInfo.getName());
        } catch (ClassNotFoundException e) {
          log.error("Class for name '{}' returned not found", classInfo.getName());
          throw new CoreException(StatusCode.CLASSNAME_NOT_FOUND);
        }
        context.catalog.singleton(clazz);
      });
    }
    return context;
  }

  public Stream<String> singletonList() {
    return catalog.inventory().keySet().stream();
  }

}
