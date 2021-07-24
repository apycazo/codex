package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Proxy using byte buddy. Thi
 */
@Slf4j
public class ProxyByteBuddy {

  public static void main(String[] args)
    throws NoSuchMethodException, InvocationTargetException,
    InstantiationException, IllegalAccessException {

    // to be used as a delegate
    Dummy defaultDummy = new Dummy("");

    Dummy dummy = new ByteBuddy()
      .subclass(Dummy.class)
      // dummy has no no-args constructor, so we need to defined one here,
      // which will call the super class default method constructor.
      // It seems like using 'Object.class.getDeclaredConstructor()' works too.
      .defineConstructor(Visibility.PUBLIC)
      .intercept(MethodCall.invoke(Dummy.class.getSuperclass().getDeclaredConstructor()).onSuper())
      .method(isDeclaredBy(Dummy.class)).intercept(MethodDelegation.to(defaultDummy))
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

    public Dummy(String s) {}

    public String echo(String text) {
      return text;
    }

    public String hi() {
      return "hello world";
    }
  }
}
