package apycazo.codex.javalin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.util.stream.Collectors;

public class JavalinCtrl {

  private static final JavalinSvc svc = new JavalinSvc();
  private static final ObjectMapper mapper = JavalinApp.mapper;

  public static void findAll(Context ctx) throws JsonProcessingException {
    ResultData data = ResultData.builder()
      .data(svc.all().collect(Collectors.toList()))
      .build();
    String payload = mapper.writeValueAsString(data);
    ctx.result(payload);
    ctx.contentType("application/json");
  }

  public static void count(Context ctx) throws JsonProcessingException {
    ResultData data = ResultData.builder()
      .data(svc.count())
      .build();
    String payload = mapper.writeValueAsString(data);
    ctx.result(payload);
  }

  public static void findId(Context ctx) throws JsonProcessingException {
    int id = ctx.pathParam("id", Integer.class).get();
    ResultData data = ResultData.builder()
      .data(svc.value(id))
      .build();
    String payload = mapper.writeValueAsString(data);
    ctx.result(payload);
  }
}
