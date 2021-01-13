package apycazo.codex.rest.features.cached;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Slf4j
public class CachedEndpoint {

  private final CachedService cachedService;

  @Autowired
  public CachedEndpoint(CachedService cachedService) {
    this.cachedService = cachedService;
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public CachedValue getById(@PathParam("id") String id) {
    log.info("Get by id '{}'", id);
    return cachedService.getById(id);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void save(CachedValue cachedValue) {
    log.info("Saving {}", cachedValue);
    cachedService.save(cachedValue);
  }

  @DELETE
  @Path("{id}")
  public void removeById(@PathParam("id") String id) {
    cachedService.removeById(id);
  }

  @DELETE
  public void clear() {
    cachedService.clear();
  }

  @GET
  @Path("report")
  @Produces(MediaType.APPLICATION_JSON)
  public CachedReport getReport() {
    return cachedService.getReport();
  }
}
