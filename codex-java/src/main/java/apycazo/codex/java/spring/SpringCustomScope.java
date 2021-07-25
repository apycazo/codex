package apycazo.codex.java.spring;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@PropertySource("classpath:application.properties")
public class SpringCustomScope {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx =
      new AnnotationConfigApplicationContext(SpringCustomScope.class);
    Dummy dummy1 = ctx.getBean(Dummy.class);
    ctx.getBean(Dummy.class); // ask for another, to check only one is created
    DummyUser user = ctx.getBean(DummyUser.class);
    String id1 = user.getDummy().getId();
    String name1 = dummy1.getName();
    log.info("Dummy id {} (delegated {}) {}", dummy1.getId(), id1, name1);
    System.setProperty("demo.name", "new-name!");
    // remove the bean, and check that the autowired value has changed too
    SandboxScopeRegister register = ctx.getBean(SandboxScopeRegister.class);
    SandboxScopeHandler handler = register.getHandler();
    handler.remove("scopedTarget.dummy");
    Dummy dummy2 = ctx.getBean(Dummy.class);
    String id2 = user.getDummy().getId();
    String name2 = dummy2.getName();
    log.info("Dummy id {} (delegated {}) {}", dummy2.getId(), id2, name2);
    log.info("Are ids different (should be true): {}", !id1.equals(id2));
    ctx.close();
  }

  @Getter
  @Component
  private static class DummyUser {
    private final Dummy dummy;

    public DummyUser(Dummy dummy) {
      this.dummy = dummy;
    }
  }

  /**
   * This only works when creating beans like this. Using @Component on the
   * class itself does not work.
   * @return the instanced bean.
   */
  @Bean
  @SandboxScoped
  public Dummy dummy(@Value("${demo.name}") String name) {
    log.info("Creating new dummy");
    return new Dummy(name);
  }

  @Getter
  public static class Dummy {
    private final String id;
    private final String name;

    public Dummy(String name) {
      this.name = name;
      id = UUID.randomUUID().toString().substring(24);
      log.info("Instanced new dummy with id {} named {}", id, name);
    }
  }

  private static final String scopeName = "sandbox";

  @Target({ ElementType.TYPE, ElementType.METHOD })
  @Retention(RetentionPolicy.RUNTIME)
  @org.springframework.context.annotation.Scope(scopeName)
  @Documented
  private @interface SandboxScoped {
    // nothing works without this line!
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;
  }

  @Configuration
  private static class SandboxScopeRegister implements BeanFactoryPostProcessor {

    private final SandboxScopeHandler handler;

    public SandboxScopeRegister() {
      this.handler = new SandboxScopeHandler();
    }

    @Override
    public void postProcessBeanFactory(
      ConfigurableListableBeanFactory beanFactory) throws BeansException {
      beanFactory.registerScope(scopeName, handler);
    }

    public SandboxScopeHandler getHandler() {
      return handler;
    }
  }

  private static class SandboxScopeHandler implements Scope {

    private final Map<String, Object> instances = new ConcurrentHashMap<>();
    private final Map<String, Runnable> callbacks = new ConcurrentHashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
      return instances.computeIfAbsent(name, k -> objectFactory.getObject());
    }

    @Override
    public Object remove(String name) {
      callbacks.remove(name);
      return instances.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
      callbacks.put(name, callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
      return null;
    }

    @Override
    public String getConversationId() {
      return null;
    }
  }
}
