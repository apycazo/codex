package apycazo.codex.hibernate.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Configuration
@ComponentScan
@PropertySource("classpath:mysql.properties")
public class SpringHibernateApp {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringHibernateApp.class);
    ctx.refresh();
    SpringDemoService app = ctx.getBean(SpringDemoService.class);
    app.runDemo();
  }
}
