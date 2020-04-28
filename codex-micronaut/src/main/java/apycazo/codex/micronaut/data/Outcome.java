package apycazo.codex.micronaut.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Outcome<T> {

  @Builder.Default
  private String timestamp = Instant.now().toString();

  @Builder.Default
  private String id = UUID.randomUUID().toString().substring(24);

  @Builder.Default
  private boolean isError = false;

  private T payload;
}
