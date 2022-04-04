package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Proxy using byte buddy. Only works as defined here when the proxied class
 * has a no-args constructor. Otherwise the correct constructor must be called,
 * or a default one must be added to the subclass with something like:
 * <pre>{@code
 * builder.defineConstructor(Visibility.PUBLIC)
 *   .intercept(MethodInvocation.invoke(ClassA.class.getDeclaredConstructor(String.class))
 *   .onSuper().withArgument("ok"))
 * }
 * </pre>
 */
@Slf4j
public class ProxyByteBuddy {

  public static void main(String[] args)
    throws NoSuchMethodException, InvocationTargetException,
    InstantiationException, IllegalAccessException {

    // to be used as a delegate
    Dummy defaultDummy = new Dummy("hola mundo!");

    Dummy dummy = new ByteBuddy()
      .subclass(Dummy.class)
      .method(isDeclaredBy(Dummy.class))
      .intercept(MethodDelegation.to(defaultDummy))
      .method(named("echo"))
      .intercept(FixedValue.value("Echo!"))
      .method(named("echo").and(takesArguments(1)).and(returns(String.class)))
      .intercept(FixedValue.value("Echo with arguments"))
      .make()
      .load(ProxyByteBuddy.class.getClassLoader())
      .getLoaded()
      .getConstructor()
      .newInstance();
    log.info("Echo -> {}", dummy.echo("eureka"));
    log.info("Hi -> {}", dummy.hi());
  }

  public static class Dummy {

    private final String salute;

    public Dummy() {
      this.salute = "hello world";
    }

    public Dummy(String salute) {
      this.salute = salute;
    }

    public String echo(String text) {
      return text;
    }

    public String hi() {
      return salute;
    }
  }
}
