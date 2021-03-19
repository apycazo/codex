package apycazo.codex.rest.common.util;

import apycazo.codex.rest.common.filter.RequestFilter;
import org.slf4j.MDC;

import static apycazo.codex.rest.common.filter.RequestFilter.MDC_REQUEST_ID_KEY;

public class FromContext {

  public static String takeRequestId() {
    return MDC.get(MDC_REQUEST_ID_KEY);
  }

  public static String takeOperationId() {
    return MDC.get(RequestFilter.MDC_OPERATION_KEY);
  }
}
