package apycazo.codex.rest.security;

import javax.ws.rs.container.ContainerRequestContext;

public interface SecurityManager {

  String getName();
  boolean authenticate(ContainerRequestContext requestContext);
}
