package apycazo.codex.rest.common.filter;

import apycazo.codex.rest.common.server.ApplicationSettings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Set;

public class CrossDomainFilter implements ContainerResponseFilter {

  public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String ACCESS_CONTROL_ALLOW_HEADER = "Access-Control-Allow-Headers";
  public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String ALLOWED_HEADERS_VALUE = "origin, content-type, accept, authorization";
  public static final String ALLOWED_METHODS_VALUE = "GET, POST, PUT, DELETE, OPTIONS, HEAD";

  private final String origins;
  private final Set<String> originList;

  @Autowired
  public CrossDomainFilter(ApplicationSettings appSettings) {
    this.origins = String.join(",", appSettings.getCorsAllowed());
    this.originList = appSettings.getCorsAllowed();
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    if (!originList.isEmpty()) {
      MultivaluedMap<String, Object> headers = responseContext.getHeaders();
      headers.add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      headers.add(ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS_VALUE);
      if (originList.contains("*")) {
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      } else {
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, origins);
      }
    }
  }
}
