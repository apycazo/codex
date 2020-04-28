package apycazo.codex.javalin;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ResultData {
  @Builder.Default
  private String responseId = UUID.randomUUID().toString().substring(24);
  @Builder.Default
  private long timestamp = Instant.now().toEpochMilli();
  @Builder.Default
  private Object data = null;
}
