package apycazo.codex.rest.common.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class JerseyRequestFilter extends RequestFilter implements ContainerRequestFilter, ContainerResponseFilter {

  public static final String METHOD_KEY = "x-method-id";
  public static final String METHOD_SYSTEM = "system";

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
  }

  /**
   * Processes the response before it is returned.
   * @param requestContext the received request context.
   * @param responseContext the response context we are returning.
   */
  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    complete(httpServletRequest, httpServletResponse);
  }
}
