package apycazo.codex.java.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Configuration
public class SpringDependencyInjection {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringDependencyInjection.class);
    ctx.refresh();
    ServiceB bean = ctx.getBean(ServiceB.class);
    log.info("Name: {}", bean.getComponentName());
  }

  @Component
  public static class ComponentA {
    public String getName() { return "component-a"; }
  }

  @Service
  @RequiredArgsConstructor
  public static class ServiceB {

    private final ComponentA a;

    public String getComponentName() {
      return a != null ? a.getName() : "null";
    }
  }
}
