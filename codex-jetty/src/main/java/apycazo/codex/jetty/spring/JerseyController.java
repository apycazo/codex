package apycazo.codex.jetty.spring;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Example of a simple jersey controller, using the created spring service.
 */
@Path("api")
public class JerseyController {

  private final SpringService service;

  @Autowired
  public JerseyController(SpringService service) {
    this.service = service;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> get() {
    Map<String, Object> map = new HashMap<>();
    map.put("controller.name", JerseyController.class.getName());
    map.put("service.name", service.getName());
    map.put("timestamp", Instant.now().toEpochMilli());
    return map;
  }
}
