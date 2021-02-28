package apycazo.codex.rest.common.util;

import apycazo.codex.rest.common.filter.ServletGatewayFilter;
import org.slf4j.MDC;

public class FromContext {

  public static String takeRequestId() {
    return MDC.get(ServletGatewayFilter.REQUEST_ID);
  }
}
