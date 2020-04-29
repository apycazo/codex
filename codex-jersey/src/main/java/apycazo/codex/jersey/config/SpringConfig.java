package apycazo.codex.jersey.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class SpringConfig {

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourceConfig() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public DefaultConversionService springConverter() {
    return new DefaultConversionService();
  }
}
