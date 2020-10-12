package es.asgarke.golem.http.dummy;

import es.asgarke.golem.http.annotations.*;
import es.asgarke.golem.http.definitions.HttpMethod;
import es.asgarke.golem.http.definitions.MediaType;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@RestResource(path = "api")
public class OperationsEndpoint {

  @Inject
  private MathService math;

  public static final String info = "{\"success\":true}";

  @Endpoint
  public String getInfoAsString() {
    return info;
  }

  @Endpoint(path = "json", produces = MediaType.APPLICATION_JSON)
  public String getInfoAsJson() {
    return info;
  }

  @Endpoint(path = "sum/:a/:b", produces = MediaType.APPLICATION_JSON)
  public Map<String, Object> sum(@PathParam("a") Integer a, @PathParam("b") Integer b) {
    Map<String, Object> content = new HashMap<>();
    content.put("a", a);
    content.put("b", b);
    content.put("result", math.sum(a, b));
    return content;
  }

  @Endpoint(path = "echo", method = HttpMethod.POST,
    consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  public Map<String, Object> echo(@Body Map<String, Object> content) {
    return content;
  }

  @Endpoint(path = "param")
  public String param(@QueryParam("value") String paramValue) {
    return paramValue;
  }


  @Endpoint(path = "exceptional")
  public String exceptional() {
    throw new RuntimeException("test-exception");
  }

}
