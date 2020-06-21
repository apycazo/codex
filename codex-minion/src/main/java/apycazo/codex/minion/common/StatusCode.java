package apycazo.codex.minion.common;

import java.util.stream.Stream;

public enum StatusCode {
  // 1xx: generic/configuration errors
  INVALID_CONFIGURATION(100, "Invalid configuration"),
  CLASSNAME_NOT_FOUND(101, "Class name not found"),
  BEAN_IS_INTERFACE(102, "Bean class is an interface"),
  BEAN_INIT_FAILED(103, "Bean init failed"),
  BEAN_CAST_FAILED(104, "Bean casting failed"),
  BEAN_INSTANCE_FAILED(105, "Bean instancing failed"),
  BASE_PACKAGE_NULL(106, "Base packages must be specified"),
  BEAN_PROVIDER_INJECTIONS(107, "A bean provider cannot depend on other injections"),
  OPERATION_NOT_SUPPORTED(108, "Operation is not supported"),
  INVOCATION_ERROR(109, "Invoking a method has failed"),
  INVALID_PROPERTY_SOURCE(110, "Invalid property source"),
  // 2xx: injection errors
  UNABLE_TO_INSTANCE(200, "Unable to instance bean"),
  UNABLE_TO_INJECT_NAMED(201, "Unable to inject named bean"),
  ALREADY_REGISTERED(203, "Bean was already registered"),
  MULTIPLE_DEFINITIONS(204, "Multiple definitions found, expected 1"),
  PROPERTY_NOT_FOUND(205, "Required property value not found"),
  PROPERTY_TYPE_ERROR(206, "Cannot convert property value not found"),
  // 3xx: consistency error
  CORRUPT_CATALOG(300, "Catalog is corrupt"),
  // 4xx: server error
  INVALID_CONTEXT(400, "Provided an invalid context"),
  INVALID_ADDRESS(401, "Unable to start server at the given address"),
  INVALID_MAPPING(402, "Endpoint provided an invalid mapping"),
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
