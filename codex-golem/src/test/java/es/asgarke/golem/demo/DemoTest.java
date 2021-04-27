package es.asgarke.golem.demo;

import es.asgarke.golem.http.GolemServer;
import es.asgarke.golem.http.definitions.MediaType;
import org.junit.jupiter.api.*;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Automatic tests for the DemoAPP, to facilitate the app evolution.
 */
@RunWith(JUnitPlatform.class)
public class DemoTest {

  private static GolemServer server;
  private static DataService service;
  private static Map<String, Object> payload;
  private static int resourceId;

  @BeforeAll
  static void setup() {
    server = GolemServer.startServer(0, DemoGolemApp.class);
    service = server.getGolemContext()
      .getFactory()
      .resolveBean(DataService.class)
      .orElseThrow(() -> new RuntimeException("Bean not found"));
    payload = Map.of("origin", "test");
    resourceId = 1;
  }

  @AfterAll
  static void stop() {
    if (server != null) {
      server.stop();
    }
  }

  @BeforeEach
  void setupTest() {
    service.clear();
  }

  @Test
  @DisplayName("config endpoint returns the expected data")
  void configEndpoint() {
    given()
      .port(server.getHttpPort())
      .when()
      .get("/api/config")
      .then()
      .statusCode(200)
      .and().body("appName", equalTo("golem-demo"))
      .and().body("path", equalTo("/"))
      .and().body("port", equalTo(9090))
      .and().body("poolSize", equalTo(4));
  }

  @Test
  @DisplayName("saving a new value works")
  void createValue() {
    given()
      .port(server.getHttpPort())
      .contentType(MediaType.APPLICATION_JSON)
      .body(payload)
      .when()
      .post("/api/data/{resourceId}", resourceId)
      .then()
      .log().all()
      .statusCode(201);
  }

  @Test
  @DisplayName("an existing value can be retrieved by id")
  void fetchById() {
    createValue();
    given()
      .port(server.getHttpPort())
      .when()
      .get("/api/data/{resourceId}", resourceId)
      .then()
      .statusCode(200)
      .and().header("Content-Type", MediaType.APPLICATION_JSON)
      .and().body("origin", equalTo("test"));
  }

  @Test
  @DisplayName("when not id is present retrieve all values")
  void fetchAll() {
    createValue();
    given()
      .port(server.getHttpPort())
      .when()
      .get("/api/data")
      .then()
      .statusCode(200)
      .and().header("Content-Type", MediaType.APPLICATION_JSON)
      .and().body("size()", equalTo(1))
      .and().body("1.origin", equalTo("test"));
  }

  @Test
  @DisplayName("values can be deleted by id")
  void deleteById() {
    createValue();
    assertThat(service.size()).isEqualTo(1);
    given()
      .port(server.getHttpPort())
      .when()
      .delete("/api/data/{resourceId}", resourceId)
      .then()
      .statusCode(200);
    assertThat(service.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("without id all values are deleted")
  void deleteAll() {
    createValue();
    assertThat(service.size()).isEqualTo(1);
    given()
      .port(server.getHttpPort())
      .when()
      .delete("/api/data")
      .then()
      .statusCode(200);
    assertThat(service.size()).isEqualTo(0);
  }
}
