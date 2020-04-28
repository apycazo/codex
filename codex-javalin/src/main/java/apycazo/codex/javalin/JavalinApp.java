package apycazo.codex.javalin;

import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.core.JavalinConfig;
import io.javalin.http.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * Try endpoints:
 * <ul>
 *   <li>/users</li>
 *   <li>/users/count</li>
 *   <li>/users/2</li>
 * </ul>
 */
@Slf4j
public class JavalinApp {

  public static void main(String[] args) {
    init().start(8080);
  }

  public static Javalin init() {
    return Javalin
      .create(JavalinApp::config)
      .exception(Exception.class, exceptionHandler())
      .routes(routes());
  }

  @NotNull
  private static ExceptionHandler<Exception> exceptionHandler() {
    return (e, ctx) -> {
      ResultData data = ResultData.builder().data(e.getMessage()).build();
      ctx.json(data);
      ctx.status(400);
    };
  }

  @NotNull
  private static EndpointGroup routes() {
    return () -> {
      path("users", () -> {
        get(JavalinCtrl::findAll);
        path("count", () -> {
          get(JavalinCtrl::count);
        });
        path(":id", () -> {
          get(JavalinCtrl::findId);
        });
      });
    };
  }

  private static void config(JavalinConfig config) {
    config.defaultContentType = "application/json";
    config.addStaticFiles("/public");
    config.enableCorsForAllOrigins();
  }

}
