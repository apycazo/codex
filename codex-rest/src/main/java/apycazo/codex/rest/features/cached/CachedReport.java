package apycazo.codex.rest.features.cached;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class CachedReport {

  private final AtomicInteger reads = new AtomicInteger(0);
  private final AtomicInteger saves = new AtomicInteger(0);

  public void read() {
    reads.incrementAndGet();
  }

  public void save() {
    saves.incrementAndGet();
  }
}
