package apycazo.codex.server.dummy;

import java.util.concurrent.atomic.AtomicInteger;

public class MonitoredInstance {

  protected static AtomicInteger instanceCounter = new AtomicInteger(0);

  public MonitoredInstance() {
    instanceCounter.incrementAndGet();
  }

  public static int getInstanceCount() { return instanceCounter.get(); }
  public static void resetInstanceCount() { instanceCounter.set(0); }
}
