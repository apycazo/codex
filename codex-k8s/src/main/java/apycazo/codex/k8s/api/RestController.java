package apycazo.codex.k8s.api;

import apycazo.codex.k8s.data.DataService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.util.List;
import java.util.Set;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.post;

public class RestController {

  private final DataService dataService;

  public static EndpointGroup configureMapping(DataService dataService) {
    RestController restController = new RestController(dataService);
    return () -> path("api", () ->
    {
      get(restController::info);
      delete(restController::remove);
      path("count", () -> get(restController::count));
      path("keys", () -> get(restController::getKeys));
      path("values", () -> get(restController::getValues));
      path(":key", () -> {
        get(restController::getById);
        delete(restController::deleteKey);
        put(restController::updateKey);
        post(restController::save);
      });
    });
  }

  public RestController(DataService dataService) {
    this.dataService = dataService;
  }

  public void info(Context ctx) {
    ctx.result("codex-k8s");
  }

  public void getById(Context ctx) {
    String key = ctx.pathParam("key");
    String value = dataService.getByKey(key);
    if (value == null) {
      notFound(ctx, key);
    } else {
      ctx.result(value);
    }
  }

  public void deleteKey(Context ctx) {
    String key = ctx.pathParam("key");
    String value = dataService.getByKey(key);
    if (value == null) {
      notFound(ctx, key);
    } else {
      dataService.deleteKey(key);
      ctx.status(200);
    }
  }

  public void updateKey(Context ctx) {
    String key = ctx.pathParam("key");
    if (dataService.getByKey(key) == null) {
      notFound(ctx, key);
    } else {
      dataService.saveOrUpdate(key, ctx.body());
      ctx.status(200);
    }
  }

  public void count(Context ctx) {
    ctx.result("count: " + dataService.getCount()).status(200);
  }

  public void save(Context ctx) {
    dataService.saveOrUpdate(ctx.pathParam("key"), ctx.body());
    ctx.status(201);
  }

  public void remove(Context ctx) {
    dataService.delete();
    ctx.status(200);
  }

  public void getKeys(Context ctx) {
    Set<String> keys = dataService.getKeys();
    ctx.result(String.join("\n---\n", keys));
  }

  public void getValues(Context ctx) {
    List<String> values = dataService.getValues();
    ctx.result(String.join("\n---\n", values));
  }

  private void notFound(Context ctx, String key) {
    ctx.result("Key '" + key + "' not found").status(404);
  }

}
