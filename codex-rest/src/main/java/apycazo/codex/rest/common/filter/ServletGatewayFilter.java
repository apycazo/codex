package apycazo.codex.rest.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This filter will be run before any jersey filters. In this case, the filter is created using 'new' while creating
 * the server. In the case of the spring context being required, override the method <code>init</code> like:
 * <pre>{@code
 * @Override
 * public void init(FilterConfig filterConfig) {
 *   ServletContext servletContext = filterConfig.getServletContext();
 *   ApplicationContext context = WebApplicationContextUtils
 *     .getRequiredWebApplicationContext(servletContext);
 * }
 * }</pre>
 * This filter will ensure that requests that are not handled by jersey will have the same set of data available and
 * leave consistent traces.
 */
@Slf4j
public class ServletGatewayFilter extends RequestFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      accept((HttpServletRequest) request);
    }
    // continue the filter chain
    chain.doFilter(request, response);
    // clear the MDC when done
    MDC.clear();
  }
}
