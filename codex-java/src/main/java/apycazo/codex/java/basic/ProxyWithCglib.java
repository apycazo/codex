package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

@Slf4j
public class ProxyWithCglib {

  public static void main(String[] args) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(Dummy.class);
    enhancer.setCallback((MethodInterceptor) (obj, method, params, proxy) -> {
      if ("echo".equals(method.getName())) {
        String result = (String)proxy.invokeSuper(obj, params);
        return "echo was '" + result + "'";
      } else {
        return proxy.invokeSuper(obj, params);
      }
    });
    Dummy dummyProxy = (Dummy)enhancer.create();
    log.info("Call 'echo' -> {}", dummyProxy.echo("eureka"));
    log.info("Call 'hi' -> {}", dummyProxy.hi());
  }

  public static class Dummy {

    public String echo(String text) {
      return text;
    }

    public String hi() {
      return "hello world";
    }
  }
}
