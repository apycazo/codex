package apycazo.codex.hibernate.h2;

import apycazo.codex.hibernate.spring.BasicUserRepository;
import apycazo.codex.hibernate.spring.SpringDemoService;
import apycazo.codex.hibernate.spring.SpringPersistenceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Using an embedded h2 database only requires having the h2 dependency in the classpath and using the
 * correct db configuration properties. <br>
 * Check file h2.properties for more info.
 */
@Slf4j
@Configuration
@PropertySource("classpath:h2.properties")
public class SpringH2Config {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringH2Config.class);
    ctx.register(SpringPersistenceConfig.class);
    ctx.register(BasicUserRepository.class);
    ctx.register(SpringDemoService.class);
    ctx.refresh();
    SpringDemoService app = ctx.getBean(SpringDemoService.class);
    app.runDemo();
  }
}
