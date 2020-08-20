package apycazo.codex.java.spring;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringValueResolver;

/**
 * Shows how to resolve properties from existing values (for example, resolving random, or cyphered values.
 */
@Slf4j
@Configuration
@Import(SpringCustomProperties.DemoService.class)
@PropertySource("classpath:demo.properties")
public class SpringCustomProperties {

  /**
   * Runs the test. Will take the properties from 'demo.properties', and modify the value for 'mutable.value' from
   * the expected value to just 'eureka!', the other property will be resolved normally.
   * @param args
   */
  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringCustomProperties.class);
    ctx.refresh();
    DemoService service = ctx.getBean(DemoService.class);
    log.info("Mutable value: '{}', String value: '{}'", service.getMutableValue(), service.getStringValue());
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer properties() {
    return new CustomPropertyResolver();
  }

  @Getter
  @Service
  public static class DemoService {

    @Value("${mutable.value:no luck!}")
    private String mutableValue;
    @Value("${string.value:no string value found}")
    private String stringValue;
  }

  static class CustomPropertyResolver extends PropertySourcesPlaceholderConfigurer {

    @Override
    protected void doProcessProperties(
      ConfigurableListableBeanFactory beanFactoryToProcess, StringValueResolver valueResolver) {
      super.doProcessProperties(beanFactoryToProcess, new CustomValueResolver(valueResolver));
    }
  }

  static class CustomValueResolver implements StringValueResolver {

    private final StringValueResolver baseResolver;

    public CustomValueResolver(StringValueResolver baseResolver) {
      this.baseResolver = baseResolver;
    }

    @Override
    public String resolveStringValue(String strVal) {
      String baseValue = baseResolver.resolveStringValue(strVal);
      log.info("Eval: {}, Resolver value: {}", strVal, baseValue);
      if ("rand".equals(baseValue)) {
        return "eureka!";
      } else {
        return baseValue;
      }
    }
  }
}
