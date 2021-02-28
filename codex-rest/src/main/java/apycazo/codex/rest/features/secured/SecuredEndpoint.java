package apycazo.codex.rest.features.secured;

import apycazo.codex.rest.common.security.Authenticated;
import apycazo.codex.rest.common.security.SecurityRole;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Slf4j
public class SecuredEndpoint {

  @GET
  @Path("admin")
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated(allowedForRoles = SecurityRole.ADMIN)
  public Map<String, Object> adminOnly() {
    return Map.of("admin", SecuredEndpoint.class.getSimpleName());
  }

  @GET
  @Path("user")
  @Produces(MediaType.APPLICATION_JSON)
  @Authenticated(allowedForRoles = SecurityRole.USER)
  public Map<String, Object> userAllowed() {
    return Map.of("user", SecuredEndpoint.class.getSimpleName());
  }
}
