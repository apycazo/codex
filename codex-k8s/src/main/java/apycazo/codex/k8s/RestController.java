package apycazo.codex.k8s;

import apycazo.codex.k8s.data.DataService;
import io.javalin.http.Context;

public class RestController {

  private final DataService dataService;

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

  public void delete(Context ctx) {
    dataService.delete();
    ctx.status(200);
  }

  private void notFound(Context ctx, String key) {
    ctx.result("Key '" + key + "' not found").status(404);
  }

}
