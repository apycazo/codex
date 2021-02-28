package apycazo.codex.rest.common.security;

import javax.ws.rs.container.ContainerRequestContext;

public interface SecurityManager {

  /**
   * Authenticates the request.
   * @param requestContext the current request context to authenticate.
   * @return true if the request contains authentication info and it is valid, false
   *         if the authentication info is not valid (e.g.: bad password).
   */
  boolean authenticate(ContainerRequestContext requestContext);

  /**
   * Gets the manager name, usually, the class name implementing it.
   * @return the manager name.
   */
  default String getName() {
    return getClass().getSimpleName();
  }
}
