package apycazo.codex.docker;

import io.javalin.Javalin;

public class DockerSampleApp {

  public static void main(String[] args) {
    Javalin app = Javalin.create().start(7000);
    app.get("/", ctx -> ctx.result("Response from docker service"));
  }
}
