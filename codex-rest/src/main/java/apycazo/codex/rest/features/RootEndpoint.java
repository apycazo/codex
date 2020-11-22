package apycazo.codex.rest.features;

import apycazo.codex.rest.features.info.InfoEndpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("")
public class RootEndpoint {

  private final ResourceContext context;

  public RootEndpoint(@Context ResourceContext context) {
    this.context = context;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> info() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("version", 1);
    map.put("ts", Instant.now().toEpochMilli());
    map.put("name", "codex-rest");
    return map;
  }

  @Path("info")
  public InfoEndpoint subresource() {
    return context.getResource(InfoEndpoint.class);
  }
}
