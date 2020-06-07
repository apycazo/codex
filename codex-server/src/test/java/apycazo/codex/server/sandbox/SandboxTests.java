package apycazo.codex.server.sandbox;

import apycazo.codex.server.dummy.TaggedClass;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SandboxTests {

  @Test
  void tryToScanPackageClasses() {
    Class<?> anchor = TaggedClass.class;
    String targetPackageName = anchor.getPackage().getName();
    try (ScanResult scanResult =                   // Assign scanResult in try-with-resources
           new ClassGraph()                        // Create a new ClassGraph instance
             .verbose()                            // If you want to enable logging to stderr
             .enableAllInfo()                      // Scan classes, methods, fields, annotations
             .whitelistPackages(targetPackageName) // Scan target package and subpackages
             .scan()) {                            // Perform the scan and return a ScanResult
      // Use the ScanResult within the try block, e.g.
      scanResult.getAllClasses().forEach(classInfo -> log.info("Info on {}", classInfo.getName()));
      // ClassInfo widgetClassInfo = scanResult.getClassInfo("com.xyz.Widget");
      // ...
    }
  }
}
