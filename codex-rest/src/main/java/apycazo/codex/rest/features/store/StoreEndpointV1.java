package apycazo.codex.rest.features.store;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class StoreEndpointV1 {

  private final StoreService storeService;

  @Autowired
  public StoreEndpointV1(StoreService storeService) {
    this.storeService = storeService;
  }

  @POST
  @Path("{key}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void storeValue(String key, Object resource) {
    storeService.store(key, resource);
  }

  @PUT
  @Path("{key}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateValue(String key, Object resource) {
    if (storeService.containsKey(key)) {
      storeService.store(key, resource);
      return Response.ok().build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Path("{key}")
  @Produces(MediaType.APPLICATION_JSON)
  public Object getValue(String key) {
    if (storeService.containsKey(key)) {
      return storeService.getValue(key);
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @DELETE
  @Path("{key}")
  public void deleteValue(String key) {
    storeService.delete(key);
  }
}
