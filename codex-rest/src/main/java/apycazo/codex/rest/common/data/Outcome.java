package apycazo.codex.rest.common.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outcome<T> {

  @Builder.Default
  private final long epochMilli = Instant.now().toEpochMilli();
  @Builder.Default
  private final boolean failure = false;
  private T data;

  public static <T> Outcome<T> success(T data) {
    return Outcome.<T>builder().failure(false).data(data).build();
  }

  public static <T> Outcome<T> failure(T data) {
    return Outcome.<T>builder().failure(true).data(data).build();
  }
}
