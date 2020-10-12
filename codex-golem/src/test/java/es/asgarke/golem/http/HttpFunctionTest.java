package es.asgarke.golem.http;

import es.asgarke.golem.http.definitions.MediaType;
import es.asgarke.golem.http.dummy.MathService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(JUnitPlatform.class)
public class HttpFunctionTest {

  private static GolemServer server;

  @BeforeAll
  static void setup() {
    server = GolemServer.startServer(0, MathService.class);
  }

  @AfterAll
  static void stop() {
    if (server != null) {
      server.stop();
    }
  }

  @Test
  void test_basic_get() {
    given().port(server.getHttpPort())
      .when()
      .get("/api")
      .then()
      .statusCode(200);
  }

  @Test
  void test_path_param() {
    String result = given().port(server.getHttpPort())
      .when()
      .get("/api/sum/5/10")
      .then()
      .log().all()
      .statusCode(200)
      .and().body("result", equalTo(15))
      .and().body("a", equalTo(5))
      .and().body("b", equalTo(10))
      .and().extract().body().asString();
    assertThat(result).isEqualTo("{\"result\":15,\"a\":5,\"b\":10}");
  }

  @Test
  void test_query_param() {
    String result = given().port(server.getHttpPort())
      .queryParam("value", "1234-abcd")
      .when()
      .get("/api/param")
      .then()
      .statusCode(200)
      .log().all()
      .and().extract().body().asString();
    assertThat(result).isEqualTo("1234-abcd");
  }

  @Test
  void test_post() {
    Map<String, Object> content = new HashMap<>();
    content.put("origin", "test");

    given().port(server.getHttpPort())
      .contentType(MediaType.APPLICATION_JSON)
      .body(content)
      .when()
      .post("/api/echo")
      .then()
      .log().all()
      .statusCode(200)
      .header("Content-Type", MediaType.APPLICATION_JSON)
      .body("origin", equalTo("test"));
  }

  @Test
  void test_missing_content_type_returns_415() {
    given().port(server.getHttpPort())
      .body("{\"origin\":\"test\"}")
      .when()
      .post("/api/echo")
      .then()
      .log().all()
      .statusCode(415);
  }

  @Test
  void test_exception_mapping() {
    given().port(server.getHttpPort())
      .when()
      .get("/api/exceptional")
      .then()
      .log().all()
      .statusCode(400)
      .body("msg", equalTo("test-exception"))
      .body("error", equalTo(true));
  }
}
