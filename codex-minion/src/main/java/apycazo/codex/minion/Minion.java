package apycazo.codex.minion;

import apycazo.codex.minion.context.ContextConfig;
import apycazo.codex.minion.context.MinionContext;
import apycazo.codex.minion.server.MinionServer;

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

  public static MinionServer startServer(Class<?> baseClass) {
    return startServer(baseClass.getPackageName());
  }

  public static MinionServer startServer(Class<?> baseClass, int port) {
    return startServer(baseClass.getPackageName(), port);
  }

  public static MinionServer startServer(String basePackageName) {
    return startServer(basePackageName, 8080);
  }

  public static MinionServer startServer(String basePackageName, int port) {
    MinionContext minionContext = init(basePackageName);
    MinionServer minionServer = new MinionServer(minionContext);
    return minionServer.port(port).start();
  }

}
