package apycazo.codex.jersey;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.net.InetSocketAddress;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

@RunWith(JUnitPlatform.class)
class JerseyAppTest {

  private HttpServer server;

  @BeforeEach
  void setup() {
    server = JerseyApp.start(0);
    InetSocketAddress address = server.getAddress();
    assertThat(address).isNotNull();
    assertThat(address.getAddress().isAnyLocalAddress()).isTrue();
  }

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop(2);
    }
  }

  @Test
  void unsecured_endpoint_requires_no_auth() {
    given().port(server.getAddress().getPort())
      .when().get("/")
      .then().statusCode(200)
      .and().body("msg", equalTo("codex-jersey"))
      .and().body("$", hasKey("id"))
      .and().body("$", hasKey("ts"))
      .and().log().all();
  }

  @Test
  void secured_endpoint_requires_auth() {
    given().port(server.getAddress().getPort())
      .when().get("/secured")
      .then().statusCode(401)
      .and().log().all();
  }

  @Test
  void secured_endpoints_responds_with_valid_auth() {
    given().port(server.getAddress().getPort())
      .auth().preemptive().basic("demo", "mysecretpwd")
      .when().get("/secured")
      .then().statusCode(200)
      .and().body("msg", equalTo("codex-jersey"))
      .and().body("$", hasKey("id"))
      .and().body("$", hasKey("ts"))
      .and().log().all();
  }

  @Test
  void subresource_responds_ok() {
    given().port(server.getAddress().getPort())
      .when().get("/subresource/a")
      .then().log().all()
      .and().statusCode(200)
      .and().body("msg", equalTo("Value A"));
  }
}