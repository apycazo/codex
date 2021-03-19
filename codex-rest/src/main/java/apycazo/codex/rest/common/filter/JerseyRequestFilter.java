package apycazo.codex.rest.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Slf4j
@Provider
public class JerseyRequestFilter extends RequestFilter implements ContainerRequestFilter, ContainerResponseFilter {

  private final ResourceInfo resourceInfo;
  private final RequestExtraData requestExtraData;
  private final HttpServletRequest httpServletRequest;
  private final HttpServletResponse httpServletResponse;

  public JerseyRequestFilter(@Context ResourceInfo resourceInfo, @Context RequestExtraData requestExtraData,
    @Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse) {
    this.resourceInfo = resourceInfo;
    this.requestExtraData = requestExtraData;
    this.httpServletRequest = httpServletRequest;
    this.httpServletResponse = httpServletResponse;
  }

  /**
   * This method processes requests that have been matched against a jersey resource. For each request, the filter
   * will check the request id & method & set the MDC values for the logger.
   * @param requestContext the current request context.
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    accept(httpServletRequest, requestExtraData, resourceInfo);
    httpServletResponse.setHeader(HEADER_REQUEST_ID, requestExtraData.getRequestId());
  }

  /**
   * Processes the response before it is returned.
   * @param requestContext the received request context.
   * @param responseContext the response context we are returning.
   */
  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    Response.StatusType statusInfo = responseContext.getStatusInfo();
    String uri = httpServletRequest.getRequestURI();
    log.info("{} {} for {}", statusInfo.getStatusCode(), statusInfo.getReasonPhrase(), uri);
    MDC.clear();
  }
}
