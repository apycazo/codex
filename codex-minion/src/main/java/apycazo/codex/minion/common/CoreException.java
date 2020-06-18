package apycazo.codex.minion.common;

import java.util.Optional;

public class CoreException extends RuntimeException {

  private final StatusCode statusCode;

  public CoreException() {
    super();
    this.statusCode = StatusCode.GENERIC_ERROR;
  }

  public CoreException(StatusCode statusCode) {
    super((statusCode = Optional.ofNullable(statusCode).orElse(StatusCode.GENERIC_ERROR)).getDescription());
    this.statusCode = statusCode;
  }

  public CoreException(StatusCode statusCode, Throwable cause) {
    super((statusCode = Optional.ofNullable(statusCode).orElse(StatusCode.GENERIC_ERROR)).getDescription(), cause);
    this.statusCode = statusCode;
  }

  public CoreException(Throwable cause) {
    super(cause);
    this.statusCode = StatusCode.GENERIC_ERROR;
  }

  public StatusCode statusCode() {
    return statusCode;
  }
}
