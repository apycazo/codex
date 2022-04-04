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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
      List<String> errors = new ArrayList<>();
      if (!"simple".equals(valueAsString)) {
        errors.add("Expected value 'simple' instead of '" + valueAsString + "'");
      }
      if (commaSeparateValuesAsList == null) {
        errors.add("Expected list not to be null");
      } else if (commaSeparateValuesAsList.size() != 3){
        errors.add("Expected list to have 3 elements");
      } else if (!String.join(",", commaSeparateValuesAsList).equals("a,b,c")) {
        errors.add("Expected list values to be 'a,b,c', found " + String.join(",", commaSeparateValuesAsList));
      }
    }
  }

}
