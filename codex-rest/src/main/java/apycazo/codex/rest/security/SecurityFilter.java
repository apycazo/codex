package apycazo.codex.rest.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Slf4j
@Authenticated
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

  private final List<SecurityManager> securityManagers;
  private final ResourceInfo resourceInfo;

  @Autowired
  public SecurityFilter(List<SecurityManager> securityManagers, @Context ResourceInfo resourceInfo) {
    this.securityManagers = securityManagers;
    this.resourceInfo = resourceInfo;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    if (securityManagers.stream().noneMatch(manager -> manager.authenticate(requestContext))) {
      requestContext.abortWith(Response.status(UNAUTHORIZED).build());
    } else {
      // check role requirements (if any)
      SecurityContext securityContext = requestContext.getSecurityContext();
      Set<SecurityRole> roleSet = new HashSet<>();
      Optional // adds class-level annotation roles
        .ofNullable(resourceInfo.getResourceClass().getAnnotation(Authenticated.class))
        .ifPresent(annotation -> roleSet.addAll(Arrays.asList(annotation.allowedForRoles())));
      Optional // adds method-level annotation roles
        .ofNullable(resourceInfo.getResourceMethod().getAnnotation(Authenticated.class))
        .ifPresent(annotation -> roleSet.addAll(Arrays.asList(annotation.allowedForRoles())));
      if (!roleSet.isEmpty() && roleSet.stream().noneMatch(role -> securityContext.isUserInRole(role.name()))) {
        requestContext.abortWith(Response.status(FORBIDDEN).build());
      }
    }
  }
}
