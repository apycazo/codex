package apycazo.codex.java.http;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

@Slf4j
public class OkHttpClientDemo {

  public static void main(String[] args) {
    int port = 9090;
    String url = "http://localhost:" + port;
    Javalin server = createTestServer(port);
    OkHttpClient client = new OkHttpClient();
    sendGetRequest(client, url);
    sendPostRequest(client, url);
    server.stop();
  }

  private static Javalin createTestServer(int port) {
    return Javalin.create().routes(() -> path("", () -> {
      get(ctx -> ctx.json(Map.of("method", "get")));
      post(ctx -> ctx.json(Map.of("method", "post", "content", ctx.body())));
    })).start(port);
  }

  private static void sendGetRequest(OkHttpClient client, String url) {
    Request request = new Request.Builder().url(url).build();
    sendRequest(client, request);
  }

  private static void sendPostRequest(OkHttpClient client, String url) {
    Request request = new Request.Builder()
      .post(RequestBody.create("demo", MediaType.parse("text/plain")))
      .url(url)
      .build();
    sendRequest(client, request);
  }

  private static void sendRequest(OkHttpClient client,  Request request) {
    try (Response response = client.newCall(request).execute();
         ResponseBody body = response.body()) {
      String content = body != null ? body.string() : "";
      if (response.isSuccessful()) {
        log.info("Get response: {}", content);
      } else {
        log.error("Failed request: {}", content);
      }
    } catch (Exception e) {
      log.error("Failed to send request", e);
    }
  }
}
