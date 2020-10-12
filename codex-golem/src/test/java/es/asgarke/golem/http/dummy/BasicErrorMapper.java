package es.asgarke.golem.http.dummy;

import es.asgarke.golem.http.definitions.MediaType;
import es.asgarke.golem.http.types.ExceptionMapper;
import es.asgarke.golem.http.types.Response;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class BasicErrorMapper implements ExceptionMapper<Exception> {

  @Override
  public boolean dealsWith(Throwable e) {
    return e instanceof Exception;
  }

  @Override
  public Response getResponse(Exception e) {
    Map<String, Object> content = new HashMap<>();
    content.put("error", true);
    content.put("msg", e.getMessage());
    return Response.builder().status(400).content(content).mediaType(MediaType.APPLICATION_JSON).build();
  }
}
