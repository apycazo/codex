package apycazo.codex.java.spring;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Shows how spring can read properties from a given file.
 */
@Slf4j
@Configuration
@PropertySource("classpath:application.properties")
public class SpringProperties {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringProperties.class);
    ctx.refresh();
  }

  /**
   * Required to process @Value config correctly.
   * @return the instanced bean.
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /**
   * Required to convert comma separated values into lists of strings.
   * @return a default conversion service instance.
   */
  @Bean
  public static ConversionService conversionService () {
    return new DefaultConversionService();
  }

  @Data
  @Component
  public static class Subject {

    @Value("${springProperties.simpleString}")
    private String valueAsString;
    @Value("${springProperties.commaListOfStrings}")
    private List<String> commaSeparateValuesAsList;

    @PostConstruct
    protected void validate() {
      log.info("Simple string value: '{}'", valueAsString);
      Optional.ofNullable(commaSeparateValuesAsList)
        .ifPresent(entry -> entry.forEach(value -> log.info("Value: '{}'", value)));
      assertThat(valueAsString).isEqualTo("simple");
      assertThat(commaSeparateValuesAsList).isNotNull();
      assertThat(commaSeparateValuesAsList.size()).isEqualTo(3);
      assertThat(commaSeparateValuesAsList.get(0)).isEqualTo("a");
      assertThat(commaSeparateValuesAsList.get(1)).isEqualTo("b");
      assertThat(commaSeparateValuesAsList.get(2)).isEqualTo("c");
    }
  }

}
