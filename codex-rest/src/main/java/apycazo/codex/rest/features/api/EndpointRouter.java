package apycazo.codex.rest.features.api;

import apycazo.codex.rest.features.cached.CachedEndpoint;
import apycazo.codex.rest.features.crud.CrudEndpoint;
import apycazo.codex.rest.features.info.InfoEndpoint;
import apycazo.codex.rest.features.secured.SecuredEndpoint;
import apycazo.codex.rest.common.server.ApplicationSettings;
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

  protected final ResourceContext context;
  protected final ApplicationSettings applicationSettings;

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

  @Path("crud")
  public CrudEndpoint dataMapEndpoint() {
    return context.getResource(CrudEndpoint.class);
  }

  @Path("cache")
  public CachedEndpoint cachedEndpoint() {
    return context.getResource(CachedEndpoint.class);
  }

  @Path("secured")
  public SecuredEndpoint securedEndpoint() {
    return context.getResource(SecuredEndpoint.class);
  }
}
