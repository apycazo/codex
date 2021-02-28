package apycazo.codex.rest.cucumber;

import apycazo.codex.rest.RestApplication;
import apycazo.codex.rest.common.data.Pair;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.eclipse.jetty.server.Server;
import org.hamcrest.Matchers;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class HttpSteps {

  private Server server;
  private int port;
  private ValidatableResponse response;
  private String content;
  private Pair<String, String> credentials;
  private final Map<String, String> headers = new HashMap<>();
  private final Map<String, String> store = new HashMap<>();

  @Given("initial conditions")
  public void initial_condition() throws Exception {
    response = null;
    content = null;
    credentials = null;
    headers.clear();
    store.clear();
    server = RestApplication.initServer();
    server.start();
    port = server.getURI().getPort();
  }

  @Given("a clear status")
  public void clear_status() {
    response = null;
    content = null;
    credentials = null;
    headers.clear();
    store.clear();
  }

  @Given("basic auth user {string} and password {string}")
  public void basic_auth_credentials(String user, String pass) {
    credentials = Pair.of(user, pass);
  }

  @Given("header {string} with value {string}")
  public void header_value(String key, String value) {
    headers.put(key, value);
  }

  @Given("^a json content like (\\.*)$")
  public void content_type_json(String content) {
    headers.put("Content-Type", MediaType.APPLICATION_JSON);
    this.content = content;
  }

  @Given("body content")
  public void body_content(String content) {
    this.content = content;
  }

  @When("I request all map values")
  public void request_all_values() {
    response = given()
      .port(port)
      .when()
      .get("api/crud")
      .then()
      .log().all();
  }

  @When("a {} request is sent to {string}")
  public void a_request_is_sent(String verb, String requestPath) {
    String path = resolveString(requestPath);
    RequestSpecification request = given().port(port).headers(headers).log().all().when();
    if (credentials != null) {
      request.given().auth().preemptive().basic(credentials.getKey(), credentials.getValue());
    }
    switch (verb.toUpperCase()) {
      case "GET":
        response = request.get(path).then().log().all();
        break;
      case "DELETE":
        response = request.delete(path).then().log().all();
        break;
      case "POST":
        if (content != null) {
          response = request.given().body(content).post(path).then().log().all();
          content = null;
        } else {
          response = request.post(path).then().log().all();
        }
        break;
      case "PUT":
        if (content != null) {
          response = request.given().body(content).put(path).then().log().all();
          content = null;
        } else {
          response = request.put(path).then().log().all();
        }
        break;
      default:
        throw new RuntimeException("Invalid param: " + verb);
    }
  }

  @Then("json path {string} matches boolean {}")
  public void json_path_matches(String path, Boolean value) {
    response.body(path, equalTo(value));
  }

  @Then("json path {string} exists")
  public void json_path_exists(String path) {
    response.body(path, Matchers.notNullValue());
  }

  @Then("json path {string} does not exist")
  public void json_path_not_exists(String path) {
    response.body(path, Matchers.nullValue());
  }

  @Then("json path {string} matches integer {}")
  public void json_path_matches(String path, Integer value) {
    response.body(path, equalTo(value));
  }

  @Then("json path {string} matches string {string}")
  public void json_path_matches(String path, String value) {
    response.body(path, equalTo(value));
  }

  @Then("json path {string} list contains {int} values")
  public void json_path_contains(String path, Integer count) {
    response.body(path, hasSize(count));
  }

  @Then("json path {string} contains {int} keys")
  public void json_path_is_empty(String path, Integer count) {
    response.body(path + ".size()", equalTo(count));
  }

  @Then("http status matches {int}")
  public void status_matches_value(Integer expectedStatus) {
    response.statusCode(expectedStatus);
  }

  @Then("^value is equal to (\\w+)$")
  public void value_is_equal_to_ok(String string) {
    assertThat(string).isEqualTo("test");
  }

  @Then("extract header {} and name it {}")
  public void extract_header_to(String headerName, String storeName) {
    String value = response.extract().header(headerName);
    store.put(storeName, value);
  }

  @After
  public void stop_server() throws Exception {
    server.stop();
  }

  private String resolveString(String key) {
    String head = "resolve:";
    if (StringUtils.hasLength(key) && key.startsWith(head)) {
      String actualKey = key.substring(head.length());
      return store.get(actualKey);
    } else {
      return key;
    }
  }
}
