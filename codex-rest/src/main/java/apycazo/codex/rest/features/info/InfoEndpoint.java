package apycazo.codex.rest.features.info;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Slf4j
public class InfoEndpoint {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> info() {
    log.info("Received request");
    return Map.of("info", "true");
  }
}
