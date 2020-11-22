package apycazo.codex.rest.server;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@ToString
@Component
public class ApplicationSettings {

  // core config
  @Value("${server.http.port:8080}")
  private int serverHttpPort;
  @Value("${server.https.port:8443}")
  private int serverHttpsPort;
  @Value("${server.stateless:true}")
  private boolean stateless;
  @Value("${server.jersey.mapping:/api}")
  private String jerseyMapping;
  @Value("${server.static.mapping:/}")
  private String staticMapping;

  // security config
  @Value("${features.security.enabled:true}")
  private boolean securityEnabled;
  @Value("${features.security.master.user:}")
  private String securityMasterUser;
  @Value("${features.security.master.pass:}")
  private String securityMasterPass;
  @Value("${features.security.cors.enabled:false}")
  private boolean corsEnabled;
  @Value("${features.security.cors.allowed:*}")
  private Set<String> corsAllowed;

  private final Map<String, Object> jettyProperties;

  public ApplicationSettings(Environment environment) {
    jettyProperties = new HashMap<>();
    if (environment instanceof AbstractEnvironment) {
      AbstractEnvironment abstractEnvironment = (AbstractEnvironment) environment;
      for (PropertySource<?> propertySource : abstractEnvironment.getPropertySources()) {
        if (propertySource instanceof MapPropertySource) {
          Map<String, Object> source = ((MapPropertySource) propertySource).getSource();
          source.forEach((k, v) -> {
            if (k.startsWith("org.eclipse.jetty.server") || k.startsWith("jetty.")) {
              jettyProperties.put(k, v);
            }
          });
        }
      }
    }
  }
}
