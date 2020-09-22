package apycazo.codex.testing.rest;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class RestService {

  public static void main(String[] args) {
    service().start(8080);
  }

  public static Javalin service() {
    Service service = new Service();
    return Javalin.create(config -> {
      config.defaultContentType = "application/json";
    }).routes(() -> {
      path("/", () -> get(ctx -> {
        Map<String, String> map = Collections.singletonMap("result", "success");
        ctx.json(map);
      }));
      path("api/:id", () -> get(service::getById));
    });
  }

  static class Service {

    final Map<Integer, String> values;

    public Service() {
      values = new HashMap<>();
      values.put(0, "zero");
      values.put(1, "one");
      values.put(2, "two");
    }

    public void getById(Context ctx) {
      String id = ctx.pathParam("id");
      String value = values.get(Integer.parseInt(id));
      Map<String, String> map = Collections.singletonMap(id, value);
      ctx.json(map);
    }
  }
}
