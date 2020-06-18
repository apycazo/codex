package apycazo.codex.minion.server;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpSettings {

  @Builder.Default
  private int port = 8080;
  @Builder.Default
  private int poolSize = 10;
  @Builder.Default
  private int backLogging = 0;

  public static HttpSettings defaults() {
    return HttpSettings.builder().build();
  }

  public static HttpSettings test() {
    return HttpSettings.builder().port(0).build();
  }

}
