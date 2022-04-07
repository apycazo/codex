package apycazo.codex.rest.cucumber;

import apycazo.codex.rest.common.data.Pair;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@CucumberContextConfiguration
@ContextConfiguration(classes = { CucumberSpringConfig.class })
// Use @DirtiesContext to load a new context per scenario
public class HttpSteps {

  @Autowired
  private CurrentState cs;

  @Given("initial conditions")
  public void initial_condition() throws Exception {
    cs.initialize();
  }

  @Given("a clear status")
  public void clear_status() {
    cs.reset();
  }

  @Given("basic auth user {string} and password {string}")
  public void basic_auth_credentials(String user, String pass) {
    cs.credentials = Pair.of(user, pass);
  }

  @Given("header {string} with value {string}")
  public void header_value(String key, String value) {
    cs.headers.put(key, value);
  }

  @Given("^a json content like (\\.*)$")
  public void content_type_json(String content) {
    cs.headers.put("Content-Type", MediaType.APPLICATION_JSON);
    cs.content = content;
  }

  @Given("body content")
  public void body_content(String content) {
    cs.content = content;
  }

  @When("I request all map values")
  public void request_all_values() {
    cs.response = given()
      .port(cs.port)
      .when()
      .get("api/crud")
      .then()
      .log().all();
  }

  @When("a {} request is sent to {string}")
  public void a_request_is_sent(String verb, String requestPath) {
    String path = resolveString(requestPath);
    RequestSpecification request = given().port(cs.port).headers(cs.headers).log().all().when();
    if (cs.credentials != null) {
      request.given().auth().preemptive().basic(cs.credentials.getKey(), cs.credentials.getValue());
    }
    switch (verb.toUpperCase()) {
      case "GET":
        cs.response = request.get(path).then().log().all();
        break;
      case "DELETE":
        cs.response = request.delete(path).then().log().all();
        break;
      case "POST":
        if (cs.content != null) {
          cs.response = request.given().body(cs.content).post(path).then().log().all();
          cs.content = null;
        } else {
          cs.response = request.post(path).then().log().all();
        }
        break;
      case "PUT":
        if (cs.content != null) {
          cs.response = request.given().body(cs.content).put(path).then().log().all();
          cs.content = null;
        } else {
          cs.response = request.put(path).then().log().all();
        }
        break;
      default:
        throw new RuntimeException("Invalid param: " + verb);
    }
  }

  @Then("json path {string} matches boolean {}")
  public void json_path_matches(String path, Boolean value) {
    cs.response.body(path, equalTo(value));
  }

  @Then("json path {string} exists")
  public void json_path_exists(String path) {
    cs.response.body(path, Matchers.notNullValue());
  }

  @Then("json path {string} does not exist")
  public void json_path_not_exists(String path) {
    cs.response.body(path, Matchers.nullValue());
  }

  @Then("json path {string} matches integer {}")
  public void json_path_matches(String path, Integer value) {
    cs.response.body(path, equalTo(value));
  }

  @Then("json path {string} matches string {string}")
  public void json_path_matches(String path, String value) {
    cs.response.body(path, equalTo(value));
  }

  @Then("json path {string} list contains {int} values")
  public void json_path_contains(String path, Integer count) {
    cs.response.body(path, hasSize(count));
  }

  @Then("json path {string} contains {int} keys")
  public void json_path_is_empty(String path, Integer count) {
    cs.response.body(path + ".size()", equalTo(count));
  }

  @Then("http status matches {int}")
  public void status_matches_value(Integer expectedStatus) {
    cs.response.statusCode(expectedStatus);
  }

  @Then("^value is equal to (\\w+)$")
  public void value_is_equal_to_ok(String string) {
    assertThat(string).isEqualTo("test");
  }

  @Then("extract header {} and name it {}")
  public void extract_header_to(String headerName, String storeName) {
    String value = cs.response.extract().header(headerName);
    cs.store.put(storeName, value);
  }

  @After
  public void stop_server() throws Exception {
    cs.server.stop();
  }

  private String resolveString(String key) {
    String head = "resolve:";
    if (StringUtils.hasLength(key) && key.startsWith(head)) {
      String actualKey = key.substring(head.length());
      return cs.store.get(actualKey);
    } else {
      return key;
    }
  }
}
