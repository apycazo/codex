package apycazo.codex.rest.cucumber;

import apycazo.codex.rest.RestApplication;
import apycazo.codex.rest.common.data.Pair;
import io.restassured.response.ValidatableResponse;
import org.eclipse.jetty.server.Server;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class CurrentState {

  public Server server;
  public int port;
  public ValidatableResponse response;
  public String content;
  public Pair<String, String> credentials;
  public final Map<String, String> headers = new HashMap<>();
  public final Map<String, String> store = new HashMap<>();

  public void initialize() throws Exception {
    response = null;
    content = null;
    credentials = null;
    headers.clear();
    store.clear();
    server = RestApplication.initServer();
    server.start();
    port = server.getURI().getPort();
  }

  public void reset() {
    response = null;
    content = null;
    credentials = null;
    headers.clear();
    store.clear();
  }

}
