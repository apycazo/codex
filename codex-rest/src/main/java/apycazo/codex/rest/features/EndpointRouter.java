package apycazo.codex.rest.features;

import apycazo.codex.rest.features.datamap.DataMapEndpoint;
import apycazo.codex.rest.features.info.InfoEndpoint;
import apycazo.codex.rest.server.ApplicationSettings;
import org.springframework.beans.factory.annotation.Autowired;

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
public class EndpointRouter {

  private final ResourceContext context;
  private final ApplicationSettings applicationSettings;

  @Autowired
  public EndpointRouter(@Context ResourceContext context, ApplicationSettings applicationSettings) {
    this.context = context;
    this.applicationSettings = applicationSettings;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> info() {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("ts", Instant.now().toEpochMilli());
    map.put("version", applicationSettings.getServiceVersion());
    map.put("name", applicationSettings.getServiceName());
    map.put("build-date", applicationSettings.getServiceBuildDate());
    return map;
  }

  @Path("info")
  public InfoEndpoint subresource() {
    return context.getResource(InfoEndpoint.class);
  }

  @Path("datamap")
  public DataMapEndpoint dataMapEndpoint() {
    return context.getResource(DataMapEndpoint.class);
  }
}
