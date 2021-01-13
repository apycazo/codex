package apycazo.codex.rest.features.cached;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachedValue {

  @Builder.Default
  private String id = UUID.randomUUID().toString();
  private String value;
}
