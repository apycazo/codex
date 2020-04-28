package apycazo.codex.javalin;

import io.javalin.http.Context;

import java.util.stream.Collectors;

public class JavalinCtrl {

  private static final JavalinSvc svc = new JavalinSvc();

  public static void findAll(Context ctx) {
    ResultData data = ResultData.builder()
      .data(svc.all().collect(Collectors.toList()))
      .build();
    ctx.json(data);
    // just as an example, not really required
    ctx.contentType("application/json");
  }

  public static void count(Context ctx) {
    ResultData data = ResultData.builder()
      .data(svc.count())
      .build();
    ctx.json(data);
  }

  public static void findId(Context ctx) {
    int id = ctx.pathParam("id", Integer.class).get();
    ResultData data = ResultData.builder()
      .data(svc.value(id))
      .build();
    ctx.json(data);
  }
}
