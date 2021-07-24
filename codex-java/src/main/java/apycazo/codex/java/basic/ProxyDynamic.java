package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class ProxyDynamic {

  public static void main(String[] args) {
    log.info("Version using a class");
    proxyWithClasses();
    log.info("Version using a lambda");
    proxyWithLambdas();
  }

  private static void proxyWithClasses() {
    Operations operations = (Operations) Proxy.newProxyInstance(
      ClassLoader.getSystemClassLoader(),
      new Class[] {Operations.class},
      new DynamicInvocationHandler()
    );
    log.info("Proxy echo response: {}", operations.echo("eureka!"));
    log.info("Proxy says hi: {}", operations.hi());
    log.info("Is proxy? {}", Proxy.isProxyClass(operations.getClass()));
  }

  private static void proxyWithLambdas() {
    Operations operations = (Operations) Proxy.newProxyInstance(
      ClassLoader.getSystemClassLoader(),
      new Class[] {Operations.class},
      (proxy, method, objects) -> {
        String name = method.getName();
        if ("echo".equals(name)) {
          return "echo -> " + objects[0];
        } else if ("hi".equals(name)) {
          return "hello world!";
        } else {
          throw new RuntimeException("Method " + name + " not implemented");
        }
      }
    );
    log.info("Proxy echo response: {}", operations.echo("eureka!"));
    log.info("Proxy says hi: {}", operations.hi());
    log.info("Is proxy? {}", Proxy.isProxyClass(operations.getClass()));
  }

  private interface Operations {
    String echo(String value);
    String hi();
  }

  private static class DynamicInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object o, Method method, Object[] objects) {
      String name = method.getName();
      if ("echo".equals(name)) {
        return "echo -> " + objects[0];
      } else if ("hi".equals(name)) {
        return "hello world!";
      } else {
        throw new RuntimeException("Method " + name + " not implemented");
      }
    }
  }

}
