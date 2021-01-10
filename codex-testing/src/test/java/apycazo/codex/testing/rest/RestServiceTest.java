package apycazo.codex.testing.rest;

import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@RunWith(JUnitPlatform.class)
class RestServiceTest {

  private static Javalin server;

  @BeforeAll
  static void setup() {
    server = RestService.service().start(0);
  }

  @AfterAll
  static void tearDown() {
    server.stop();
  }

  @Test
  void test_get_id() {
    given()
      .port(server.port())
      .when()
      .get("api/{id}", 0)
      .then()
      .statusCode(200)
      .body("0", equalTo("zero"));
  }
}