package apycazo.codex.jersey.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Slf4j
@Provider
@RequiresAuth
@Priority(Priorities.AUTHENTICATION)
public class BasicAuthenticationFilter implements ContainerRequestFilter {

  @Context
  private UriInfo uriInfo;
  @Autowired
  private SecurityService securityService;

  private final Response unauthorized = Response.status(UNAUTHORIZED).build();

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String authHdr = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    BasicCredentials credentials = BasicCredentials.fromHeader(authHdr);
    if (!securityService.validateCredentials(credentials)) {
      log.info("Invalid credentials provided");
      requestContext.abortWith(unauthorized);
    } else {
      log.info("Valid credentials found");
      ApiSecurityContext securityContext = new ApiSecurityContext(uriInfo, credentials);
      requestContext.setSecurityContext(securityContext);
    }
  }
}
