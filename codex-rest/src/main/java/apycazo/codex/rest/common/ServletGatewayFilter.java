package apycazo.codex.rest.common;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class ServletGatewayFilter implements Filter {

  public static final String REQUEST_ID = "x-request-id";
  public static final String OPERATION_NAME = "x-operation-name";
  public static final String SYSTEM_OPERATION_NAME = "system";
  public static final String SYSTEM_CID = "000000000000";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    // generate a request id
    String requestId = UUID.randomUUID().toString().substring(24);
    if (request instanceof HttpServletRequest) {
      // if there was already a request id (frame-id, actually), replace the requestId
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      httpRequest.getHeader(REQUEST_ID);
      requestId = Optional.ofNullable(httpRequest.getHeader(REQUEST_ID)).orElse(requestId);
    }
    MDC.put(REQUEST_ID, requestId);
    // set the operation as system, since we have not matched a resource yet
    MDC.put(OPERATION_NAME, SYSTEM_OPERATION_NAME);
    // set the outgoing values
    if (response instanceof HttpServletResponse) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setHeader(REQUEST_ID, requestId);
    }
    MDC.put(REQUEST_ID, SYSTEM_CID);
    MDC.put(JerseyGatewayFilter.METHOD_KEY, JerseyGatewayFilter.METHOD_SYSTEM);
    // continue the filter chain
    chain.doFilter(request, response);
  }
}
