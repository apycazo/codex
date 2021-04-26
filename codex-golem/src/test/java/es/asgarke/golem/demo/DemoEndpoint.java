package es.asgarke.golem.demo;

import es.asgarke.golem.http.annotations.Body;
import es.asgarke.golem.http.annotations.Endpoint;
import es.asgarke.golem.http.annotations.PathParam;
import es.asgarke.golem.http.annotations.RestResource;
import es.asgarke.golem.http.definitions.HttpMethod;
import es.asgarke.golem.http.definitions.MediaType;
import es.asgarke.golem.http.types.Response;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

@RestResource(path = "/api")
public class DemoEndpoint {

  private final DataService service;
  private final ConfigHolder config;

  @Inject
  public DemoEndpoint(DataService service, ConfigHolder config) {
    this.service = service;
    this.config = config;
  }

  @Endpoint(method = HttpMethod.GET, path = "/config", produces = MediaType.APPLICATION_JSON)
  public ConfigHolder getConfig() {
    return config;
  }

  @Endpoint(method = HttpMethod.POST, path = "data/:id", consumes = MediaType.APPLICATION_JSON)
  public Response put(@PathParam("id") String key, @Body Object value) {
    int okStatus = service.exists(key) ? 200 : 201;
    int status = service.put(key, value) ? okStatus : 400;
    return Response.status(status);
  }

  @Endpoint(method = HttpMethod.GET, path = "data/:id", produces = MediaType.APPLICATION_JSON)
  public Response get(@PathParam("id") String key) {
    Optional<Object> value = service.get(key);
    if (value.isPresent()) {
      return Response.ok().json(value.get());
    } else {
      return Response.notFound();
    }
  }

  @Endpoint(method = HttpMethod.GET, path = "data/first", produces = MediaType.APPLICATION_JSON)
  public Response first() {
    Object value = service.all().values().stream().findFirst().orElse(null);
    return value != null ? Response.ok().json(value) : Response.notFound();
  }

  @Endpoint(method = HttpMethod.GET, path = "data", produces = MediaType.APPLICATION_JSON)
  public Map<String, Object> all() {
    return service.all();
  }

  @Endpoint(method = HttpMethod.DELETE, path = "data/:id")
  public Response delete(@PathParam("id") String key) {
    if (service.exists(key)) {
      service.remove(key);
      return Response.ok();
    } else {
      return Response.notFound();
    }
  }

  @Endpoint(method = HttpMethod.DELETE, path = "data")
  public Response clear() {
    service.clear();
    return Response.ok();
  }
}
