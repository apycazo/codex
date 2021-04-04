package apycazo.codex.rest.common.status;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class StatusReport {

  private final String name;
  private final StatusValue status;
  private final String msg;
}
