package apycazo.codex.java.basic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class Reflection {

  public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
    log.info("=== getFields");
    // includes public fields (type, baseType, one, three), including accessible inherited fields
    Field[] fields = Dummy.class.getFields();
    Arrays.stream(fields).forEach(field -> log.info("Name: {}, Type: {}", field.getName(), field.getType()));
    log.info("=== getDeclaredFields");
    // public & private fields, but does not include inherited fields
    fields = Dummy.class.getDeclaredFields();
    Arrays.stream(fields).forEach(field -> log.info("Name: {}, Type: {}", field.getName(), field.getType()));
    // --- method calls
    Dummy dummy = new Dummy();
    Method getOne = Dummy.class.getMethod("getOne");
    Object invoke = getOne.invoke(dummy);
    log.info("=== getOne response: {}", invoke.toString());
    Method echoString = Dummy.class.getMethod("echo", String.class);
    log.info("=== Echo (x): {}", echoString.invoke(dummy, "x"));
    Method echoInt = Dummy.class.getMethod("echo", int.class);
    log.info("=== Echo (1): {}", echoInt.invoke(dummy, 1));
    // --- instance class
    Constructor<?>[] constructors = Dummy.class.getConstructors();
    for (Constructor<?> constructor : constructors) {
      log.info("=== Constructor {}, params: {}", constructor.getName(), constructor.getParameterCount());
    }
    Constructor<Dummy> constructor = Dummy.class.getConstructor(int.class);
    Dummy secondDummy = constructor.newInstance(10);
    log.info("=== Second dummy (int param) one: {}", secondDummy.getOne());
    constructor = Dummy.class.getConstructor();
    secondDummy = constructor.newInstance();
    log.info("=== Second dummy (no params) one: {}", secondDummy.getOne());
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  private static class Dummy extends Base {

    public static final String type = "dummy-class";
    private static final String privateType = "private";

    public int three = 3;
    private int four = 4;

    public Dummy() {}

    public Dummy(int one) {
      this.one = one;
    }

    public String echo(String text) {
      return "echo(txt):" + text;
    }

    public String echo(int number) {
      return "echo(int):" + number;
    }
  }

  @Data
  private static class Base {
    public static final String baseType = "base-class";

    public int one = 1;
    private int two = 2;
  }
}
