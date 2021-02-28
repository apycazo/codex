package apycazo.codex.rest.common.filter;

import org.slf4j.MDC;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.lang.reflect.Method;

public class JerseyGatewayFilter implements ContainerRequestFilter {

  public static final String METHOD_KEY = "x-method-id";
  public static final String METHOD_SYSTEM = "system";

  private final ResourceInfo resourceInfo;

  public JerseyGatewayFilter(@Context ResourceInfo resourceInfo) {
    this.resourceInfo = resourceInfo;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    Class<?> resourceClass = resourceInfo.getResourceClass();
    Method resourceMethod = resourceInfo.getResourceMethod();
    String operation = resourceClass.getSimpleName() + ":" + resourceMethod.getName();
    MDC.put(METHOD_KEY, operation);
  }
}
