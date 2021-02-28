package apycazo.codex.rest.features.crud;

import apycazo.codex.rest.common.data.Outcome;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * A simple sub resource, holding a key-value map (kinda like a cache). The purpose of this endpoint is
 * just to test the basic CRUD operations (create, read, update, delete).
 */
public class CrudEndpoint {

  private final CrudService service;
  private final ContainerRequestContext request;
  private final ContainerResponseContext response;

  @Autowired
  public CrudEndpoint(CrudService service, @Context ContainerRequestContext request,
    @Context ContainerResponseContext response) {
    this.service = service;
    this.request = request;
    this.response = response;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Outcome<Map<String, String>> getAll() {
    return Outcome.success(service.getAll());
  }

  @GET
  @Path("{key}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getValueForKey(@PathParam("key") String key) {
    if (service.isKeyPresent(key)) {
      return Response.ok(Outcome.success(service.getValueForKey(key))).build();
    } else {
      return Response.status(HttpStatus.NOT_FOUND_404).entity(Outcome.failure("Key '" + key + "' not found")).build();
    }
  }

  /**
   * The POST operation is not <b>idempotent</b>, and thus, trying to create a resource with an existing key will
   * return an error (bad request, as per the definition at https://tools.ietf.org/html/rfc7231#page-58:
   * <q>the server cannot or will not process the request due to something that is perceived to be a client error
   * (e.g., malformed request syntax, invalid request message framing, or deceptive request routing)</q>
   *
   * @param key   the key to store the value at.
   * @param value the actual value to store.
   * @return http status 201 when the resource is correctly created, otherwise the status 400 will be returned instead.
   * @throws URISyntaxException on the rare case where the relative URI gives an error (e.g.: invalid character is part
   *                            of the key.
   */
  @POST
  @Path("{key}")
  @Consumes(MediaType.TEXT_PLAIN)
  public Response setValueForKey(@PathParam("key") String key, String value) throws URISyntaxException {
    if (service.isKeyPresent(key)) {
      Outcome<String> msg = Outcome.failure("Key '" + key + "' already exists");
      return Response.status(HttpStatus.BAD_REQUEST_400).entity(msg).type(MediaType.APPLICATION_JSON).build();
    } else {
      service.setValueForKey(key, value);
      return Response.created(new URI(request.getUriInfo().getPath())).build();
    }
  }

  @PUT
  @Path("{key}")
  @Consumes(MediaType.TEXT_PLAIN)
  public void updateValueForKey(@PathParam("key") String key, String value) {
    if (service.isKeyPresent(key)) {
      service.setValueForKey(key, value);
    } else {
      response.setStatus(HttpStatus.NOT_FOUND_404);
    }
  }

  @DELETE
  @Path("{key}")
  public void deleteKeyValue(@PathParam("key") String key) {
    if (service.isKeyPresent(key)) {
      service.deleteKey(key);
    } else {
      response.setStatus(HttpStatus.NOT_FOUND_404);
    }
  }

  @GET
  @Path("size")
  @Produces(MediaType.APPLICATION_JSON)
  public Outcome<Integer> getSize() {
    return Outcome.success(service.getSize());
  }

  @DELETE
  public void removeAllKeys() {
    service.clear();
  }
}
