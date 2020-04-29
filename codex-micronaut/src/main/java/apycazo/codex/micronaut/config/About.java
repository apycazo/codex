package apycazo.codex.micronaut.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties("app")
public class About {

  private String author;
  private String version;
}
