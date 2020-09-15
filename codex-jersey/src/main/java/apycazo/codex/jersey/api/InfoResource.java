package apycazo.codex.jersey.api;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class InfoResource {

  @Builder.Default
  private String id = UUID.randomUUID().toString().substring(24);
  @Builder.Default
  private long ts = Instant.now().toEpochMilli();
  private String msg;
}
