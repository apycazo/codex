package apycazo.codex.java.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * Demonstrates how to implement a simple remote config server.
 * <li>A Javalin based rest api is used to serve a config map from http port 8080.</li>
 * <li>The resource server URL is read from a property file 'remote.properties'.</li>
 * <li>The remote properties are resolved using the custom <code>RemotePropertySource</code>.</li>
 * <li>The demo will show what property has the bean <code>SpringService</code> been provided with.</li>
 */
@Slf4j
public class SpringPropertySource {

  public static void main(String[] args) {
    // property map
    Map<String, String> properties = Map.of("property.name", "eureka");
    // property server
    log.info("Creating property server");
    PropertyServer propertyServer = new PropertyServer(properties, 8080);
    log.info("Creating spring context");
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringConfig.class);
    ctx.refresh();
    SpringService bean = ctx.getBean(SpringService.class);
    log.info("Bean value: {}", bean.getValue());
    propertyServer.stop();
  }

  @Configuration
  @Import(SpringService.class)
  @org.springframework.context.annotation.PropertySource(
    value = "remote.properties")
  @org.springframework.context.annotation.PropertySource(
    value = "${remote.target}", factory = RemotePropertySource.class)
  public static class SpringConfig {}

  @Service
  public static class SpringService {

    @Getter
    private final String value;

    public SpringService(@Value("${property.name:unknown}") String prop) {
      this.value = prop;
    }
  }

  /**
   * Implements a very basic property server, which will just provide the config map given at the required port.
   */
  public static class PropertyServer {

    private final Javalin javalin;

    public PropertyServer(Map<String, String> properties, int port) {
      javalin = Javalin
        .create()
        .routes(() -> path("", () -> get(ctx -> ctx.json(properties))))
        .start(port);
    }

    public void stop() {
      javalin.stop();
    }
  }

  /**
   * This class implements the actual property resolver. The resource received (expected but not tested to be an URL)
   * will be resolved and mapper with an ObjectMapper instance and used to return a property source.
   */
  public static class RemotePropertySource implements PropertySourceFactory {

    private final ObjectMapper mapper = new ObjectMapper();

    @NotNull
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
      URL url = resource.getResource().getURL();
      log.info("Resource target is {}", url);
      Properties properties = mapper.readValue(url, Properties.class);
      log.info("Properties read: {}", properties);
      return new PropertiesPropertySource(url.toString(), properties);
    }
  }
}
