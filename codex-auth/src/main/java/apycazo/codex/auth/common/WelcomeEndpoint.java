package apycazo.codex.auth.common;

import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Path("welcome")
public class WelcomeEndpoint {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> welcome(@Context HttpSession session) {
    Map<String, Object> map = new HashMap<>();
    map.put("ts", Instant.now().toString());
    if (SessionUtil.isAuthenticatedSession(session)) {
      map.put("authenticated", "true");
    } else {
      map.put("authenticated", "false");
    }
    return map;
  }
}
