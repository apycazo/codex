package apycazo.codex.minion;

import apycazo.codex.minion.context.ContextConfig;
import apycazo.codex.minion.context.MinionContext;

import java.util.Optional;

public class Minion {

  public static MinionContext run(Class<?> baseClass) {
    return run(baseClass.getPackageName());
  }

  public static MinionContext run(String basePackage) {
    return ContextConfig.basePackages(basePackage).start();
  }

  private String[] basePackageNames;
  private MinionContext minionContext = null;

//  public static ContextConfig basePackages(String ... packageNames) {
//    ContextConfig ctx = new ContextConfig();
//    if (packageNames == null || packageNames.length == 0) {
//      throw new CoreException(BASE_PACKAGE_NULL);
//    } else {
//      ctx.basePackageNames = packageNames;
//    }
//    return ctx;
//  }

  public MinionContext start() {
    minionContext = new MinionContext(basePackageNames);
    return minionContext;
  }

  public Optional<MinionContext> getContext() {
    return Optional.ofNullable(minionContext);
  }

  // sample run:
  // Minion.server()
  //  .properties()
  //  .basePath(Test.class)
  //  .port(8989)
  //  .run();

  // Minion.context().basePath().run()
  // Minion.run(basePath);
}
