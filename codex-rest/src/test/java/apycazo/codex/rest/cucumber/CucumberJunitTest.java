package apycazo.codex.rest.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
  features = { "src/test/resources/features" },
  glue = "apycazo.codex.rest.cucumber",
  plugin = {"pretty"})
public class CucumberJunitTest {
}
