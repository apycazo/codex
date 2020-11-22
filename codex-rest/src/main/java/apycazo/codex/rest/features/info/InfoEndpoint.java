package apycazo.codex.rest.features.info;

import apycazo.codex.rest.security.Authenticated;
import apycazo.codex.rest.security.SecurityRole;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Slf4j
@Authenticated(allowedForRoles = SecurityRole.ADMIN)
public class InfoEndpoint {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> info() {
    log.info("Received request");
    return Map.of("info", "true");
  }

}
