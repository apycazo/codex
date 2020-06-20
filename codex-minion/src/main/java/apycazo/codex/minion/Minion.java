package apycazo.codex.minion;

import apycazo.codex.minion.context.ContextConfig;
import apycazo.codex.minion.context.MinionContext;

public class Minion {

  public static MinionContext init(Class<?> baseClass) {
    return init(baseClass.getPackageName());
  }

  public static MinionContext init(String basePackageName) {
    return ContextConfig.basePackages(basePackageName).load();
  }

  public static MinionContext start(Class<?> baseClass) {
    return start(baseClass.getPackageName());
  }

  public static MinionContext start(String basePackageName) {
    return init(basePackageName).start();
  }

}
