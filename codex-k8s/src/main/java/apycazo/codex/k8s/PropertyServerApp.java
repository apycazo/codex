package apycazo.codex.k8s;

import apycazo.codex.k8s.api.RestController;
import apycazo.codex.k8s.config.ServiceProperties;
import apycazo.codex.k8s.data.DataService;
import apycazo.codex.k8s.data.InMemoryDataService;
import apycazo.codex.k8s.data.MysqlDataService;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.core.JavalinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PropertyServerApp {

  private static final Logger log =
    LoggerFactory.getLogger(PropertyServerApp.class);
  private static final String APP_CFG = "app_cfg";

  public static void main(String[] args) {
    ServiceProperties properties = new ServiceProperties(
      System.getProperty(APP_CFG, System.getenv(APP_CFG)), args
    );
    log.info("Creating service with properties:\n{}", properties);
    DataService dataService = resolveDataService(properties);
    log.info("Resolved data service class '{}'", dataService.getClass());
    Javalin
      .create(PropertyServerApp::config)
      .routes(RestController.configureMapping(dataService))
      .start(properties.getPort());
  }

  public static DataService resolveDataService(ServiceProperties properties) {
    String dbhost = properties.getDbhost();
    if (dbhost != null && !dbhost.isEmpty()) {
      return new MysqlDataService(properties);
    } else {
      return new InMemoryDataService();
    }
  }

  private static void config(JavalinConfig config) {
    config.defaultContentType = "text/plain";
    config.enableCorsForAllOrigins();
  }
}
