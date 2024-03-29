package apycazo.codex.rest.common.error;

import lombok.Getter;
import org.slf4j.MDC;

import static apycazo.codex.rest.common.filter.RequestFilter.MDC_REQUEST_ID_KEY;

public class ServiceException extends RuntimeException {

  @Getter
  private ErrorInfo errorInfo;

  public ServiceException(String msg) {
    super(msg);
    initErrorInfo(msg, ErrorCode.GenericError);
  }

  public ServiceException(String msg, int code) {
    super(msg);
    initErrorInfo(msg, code);
  }

  public ServiceException(String msg, int code, Throwable throwable) {
    super(msg, throwable);
    initErrorInfo(msg, code);
  }

  private void initErrorInfo(String msg, int code) {
    String requestId = MDC.get(MDC_REQUEST_ID_KEY);
    errorInfo = ErrorInfo.builder().message(msg).errorCode(code).requestId(requestId).build();
  }
}
