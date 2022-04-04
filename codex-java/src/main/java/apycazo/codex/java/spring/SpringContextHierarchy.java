package apycazo.codex.java.spring;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Shows how to use context inheritance and property customization. This example
 * can be used along with the code in 'SpringCustomProperties' to create a
 * source of external properties before starting the actual application context.
 * <p>
 * What this does, is to create a bootstrapping context (the parent context), to
 * resolve the service properties ahead of time. For example, this allows to
 * query a property server, and when all properties are present, init the child
 * context.
 */
@Slf4j
public class SpringContextHierarchy {

  public static void main(String[] args) {
    // --- create parent context
    System.setProperty("system.value1", "success");
    AnnotationConfigApplicationContext parentContext;
    parentContext = new AnnotationConfigApplicationContext();
    parentContext.register(ParentConfig.class);
    parentContext.refresh();
    // --- create child context: will inherit beans & properties from parent
    System.setProperty("system.value2", "success");
    ConfigurableEnvironment environment = parentContext.getEnvironment();
    AnnotationConfigApplicationContext childContext;
    childContext = new AnnotationConfigApplicationContext();
    childContext.register(ChildConfig.class);
    childContext.setParent(parentContext);
    // setting the environment does not seem to be required, but it is used
    // in many examples, so I am including it here.
    childContext.setEnvironment(environment);
    childContext.refresh();
    // --- get the test bean
    TestHandler testHandler = childContext.getBean(TestHandler.class);
    // this value is defined only in the system as 'success'
    log.info("System 1  : {}", testHandler.getSystem1Value());
    // this value is defined only in the system as 'success'
    log.info("System 2  : {}", testHandler.getSystem2Value());
    // this value is defined only in the parent as 'success' from file
    log.info("Parent    : {}", testHandler.getParentValue());
    // this value is defined only in the child as 'success' from file
    log.info("Child     : {}", testHandler.getChildValue());
    // this value is defined only in the parent as 'success' from code
    log.info("Custom    : {}", testHandler.getCustomValue());
    // this value is defined only in the parent as 'success' from code
    log.info("List      : {}", testHandler.getCustomNumbers());
    // the parent-defined properties override those found in the child context.
    log.info("Override  : {}", testHandler.getOverriddenBy());
    // this value is taken from the child properties, requiring a parent value
    log.info("Composite : {}", testHandler.getComposite());
    // this value is defined in two custom properties, and requires expanding
    log.info("Expanded (custom) : {}", testHandler.getExpandedCustom());
    // this value is defined in custom/child properties, and requires expanding
    log.info("Expanded (child)  : {}", testHandler.getExpandedChild());
    // the 'dummy' bean is only defined in the parent context
    log.info("Dummy?    : {}", testHandler.getDummy() != null);
  }

  @Configuration
  @PropertySource("classpath:parent.properties")
  public static class ParentConfig {

    public ParentConfig(ConfigurableEnvironment environment) {
      Properties properties = new Properties();
      properties.put("custom.value", "success");
      properties.put("custom.numbers", "1,2,3,4");
      properties.put("prop.base", "base");
      properties.put("prop.expanded.custom", "${prop.base:unknown}-custom");
      MutablePropertySources sources = environment.getPropertySources();
      String property = environment.getProperty("custom.numbers");
      // this logs the value '1,2', which is the value read from the properties
      // file before this method overrides it. This behavior is useful it we
      // want to define in the file itself if the properties there have
      // precedence over the custom values (addLast, below) or not (addFirst).
      log.info("Key 'custom.numbers' is {} on parent init", property);
      // if this method is addFirst, then the properties defined here will have
      // precedence over those defined in 'parent.properties', when 'addLast' is
      // used instead, the properties file will have precedence.
      sources.addFirst(new PropertiesPropertySource("custom", properties));
    }

    @Bean // inherited by the child context
    public ConversionService conversionService() {
      return new DefaultConversionService();
    }

    @Bean // inherited by the child context
    public Dummy dummy() {
      return new Dummy();
    }
  }

  @Configuration
  @Import(TestHandler.class)
  // the properties defined here will only be used when not found in the
  // parent context.
  @PropertySource("classpath:child.properties")
  public static class ChildConfig {}

  @Getter
  @Service
  public static class TestHandler {
    @Value("${system.value1:failure}")
    private String system1Value;
    @Value("${system.value2:failure}")
    private String system2Value;
    @Value("${parent.value:failure}")
    private String parentValue;
    @Value("${child.value:failure}")
    private String childValue;
    @Value("${custom.value:failure}")
    private String customValue;
    @Value("${custom.numbers:0}")
    private List<Integer> customNumbers;
    @Value("${overriddenBy:none}")
    private String overriddenBy;
    @Value("${composite.value:none}")
    private String composite;
    @Value("${prop.expanded.custom:unknown}")
    private String expandedCustom;
    @Value("${prop.base:unknown}-child")
    private String expandedChild;
    @Autowired(required = false)
    private Dummy dummy;
  }

  /**
   * Dummy class for injection testing.
   */
  private static class Dummy {}
}
