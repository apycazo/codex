package es.asgarke.golem.core;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

class InspectorTest {

  @Test
  void bean_is_pure_inspection() {
    assertThat(Inspector.isPureDefinition(A.class)).isTrue();
    assertThat(Inspector.isPureDefinition(B.class)).isTrue();
    assertThat(Inspector.isPureDefinition(C.class)).isTrue();
    assertThat(Inspector.isPureDefinition(D.class)).isTrue();
  }

  @Test
  void bean_is_not_pure_inspection() {
    assertThat(Inspector.isPureDefinition(E.class)).isFalse();
    assertThat(Inspector.isPureDefinition(F.class)).isFalse();
  }

  static class A {
  }

  static class B {
    public B() {
    }
  }

  private static class C {
  }

  private static class D {
    public D() {
    }
  }

  static class E {
    @Inject
    A a;
  }

  static class F {
    @Inject
    private F() {
    }
  }

}