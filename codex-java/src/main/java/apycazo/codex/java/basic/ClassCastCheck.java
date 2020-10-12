package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassCastCheck {

  private interface X {}
  private static class A implements X {}
  private static class B extends A {}

  public static void main(String[] args) {
    log.info("B assignable from A ? {}", B.class.isAssignableFrom(A.class)); // false
    log.info("A assignable from B ? {}", A.class.isAssignableFrom(B.class)); // true
    log.info("B assignable from B ? {}", B.class.isAssignableFrom(B.class)); // true
    log.info("X assignable from B ? {}", X.class.isAssignableFrom(B.class)); // true
  }
}
