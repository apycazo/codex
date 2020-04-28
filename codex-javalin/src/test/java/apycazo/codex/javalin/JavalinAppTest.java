package apycazo.codex.javalin;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasKey;

class JavalinAppTest {

  private Javalin app = JavalinApp.init();

  @BeforeEach
  void deploy() {
    app.start(0);
    RestAssured.port = app.port();
  }

  @AfterEach
  void unDeploy() {
    app.stop();
  }

  @Test
  void responses_match_structure() {
    when()
      .get("/users").then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("$", hasKey("data"))
      .body("$", hasKey("timestamp"))
      .body("$", hasKey("responseId"));
  }

  @Test
  void get_users_return_all_values() {
    when()
      .get("/users").then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("data", hasItems("john", "fox", "dana"));
  }

  @Test
  void get_count_equals_three() {
    when()
      .get("users/count").then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("data", equalTo(3));
  }

  @Test
  void get_id_returns_expected() {
    when()
      .get("users/1").then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("data", equalTo("dana"));
  }

  @ParameterizedTest
  @CsvSource({"0, john", "1, dana", "2, fox"})
  void all_ids_are_present(int id, String expectedValue) {
    given()
      .pathParam("id", id)
      .when()
      .get("users/{id}").then()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("data", equalTo(expectedValue));
  }
}