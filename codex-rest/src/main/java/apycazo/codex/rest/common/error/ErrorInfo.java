package apycazo.codex.rest.common.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo {

  private String requestId;
  private int errorCode;
  private String message;
}
