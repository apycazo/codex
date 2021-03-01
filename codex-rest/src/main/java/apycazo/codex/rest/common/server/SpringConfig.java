package apycazo.codex.rest.common.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@PropertySource(value = "classpath:build-info.properties")
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:application.properties", ignoreResourceNotFound = true)
public class SpringConfig {

  /**
   * Registers a placeholder configurator, required to resolve <code>@Value</code>
   * annotations.
   * @return the generated bean.
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourceConfig() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /**
   * A value converter (to map properties into lists/maps for example).
   * @return the generated bean.
   */
  @Bean
  public DefaultConversionService springConverter() {
    return new DefaultConversionService();
  }

}
