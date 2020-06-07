package apycazo.codex.server.errors;

import java.util.stream.Stream;

public enum StatusCode {
  // 1xx: generic/configuration errors
  INVALID_CONFIGURATION(100, "Invalid configuration"),
  CLASSNAME_NOT_FOUND(101, "Class name not found"),
  BEAN_INIT_FAILED(102, "Bean init failed"),
  // 2xx: injection errors
  UNABLE_TO_INSTANCE(200, "Unable to instance bean"),
  UNABLE_TO_INJECT_QUALIFIED(201, "Unable to inject named bean"),
  // 9xx: unknown
  GENERIC_ERROR(1000, "Generic error"),
  UNDEFINED_STATUS(1001, "Undefined status"),;

  StatusCode(int code, String description) {
    this.code = code;
    this.description = description;
  }

  private final int code;
  private final String description;

  public StatusCode from(int code) {
    return Stream.of(StatusCode.values())
      .filter(st -> st.code == code)
      .findFirst()
      .orElse(StatusCode.UNDEFINED_STATUS);
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "Status:" + code + ":" + description;
  }
}
