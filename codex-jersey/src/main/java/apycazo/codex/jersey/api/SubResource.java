package apycazo.codex.jersey.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class SubResource {

  private static final Map<String, InfoResource> infoMap;

  static {
    infoMap = new HashMap<>();
    infoMap.put("a", InfoResource.builder().id("a").msg("Value A").build());
    infoMap.put("b", InfoResource.builder().id("b").msg("Value B").build());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, InfoResource> findAll() {
    return infoMap;
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public InfoResource findById(@PathParam("id") String id) {
    return infoMap.get(id);
  }
}
