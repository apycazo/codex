package apycazo.codex.java.spring;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Slf4j
public class SpringContextHierarchy {

  public static void main(String[] args) {
    // --- create parent context
    AnnotationConfigApplicationContext parentContext;
    parentContext = new AnnotationConfigApplicationContext();
    parentContext.register(ParentConfig.class);
    parentContext.refresh();
    // --- create child context: will inherit beans & properties from parent
    AnnotationConfigApplicationContext childContext;
    childContext = new AnnotationConfigApplicationContext();
    childContext.register(ChildConfig.class);
    childContext.setParent(parentContext);
    childContext.refresh();
    // --- get the test bean
    TestHandler testHandler = childContext.getBean(TestHandler.class);
    log.info("Parent : {}", testHandler.getParentValue());
    log.info("Child  : {}", testHandler.getChildValue());
    log.info("Custom : {}", testHandler.getCustomValue());
    log.info("List   : {}", testHandler.getCustomNumbers());
    log.info("Count  : {}", testHandler.getCustomNumbers().size());
  }

  @Configuration
  @PropertySource("parent.properties")
  public static class ParentConfig {

    public ParentConfig(ConfigurableEnvironment environment) {
      Properties properties = new Properties();
      properties.put("custom.value", "success");
      properties.put("custom.numbers", "1,2,3,4");
      MutablePropertySources sources = environment.getPropertySources();
      sources.addLast(new PropertiesPropertySource("custom", properties));
    }

    @Bean // inherited by the child context
    public ConversionService conversionService() {
      return new DefaultConversionService();
    }
  }

  @Configuration
  @Import(TestHandler.class)
  @PropertySource("child.properties")
  public static class ChildConfig {}

  @Getter
  @Service
  public static class TestHandler {
    @Value("${parent.value:failure}")
    private String parentValue;
    @Value("${child.value:failure}")
    private String childValue;
    @Value("${custom.value:failure}")
    private String customValue;
    @Value("${custom.numbers:0}")
    private List<Integer> customNumbers;
  }
}
