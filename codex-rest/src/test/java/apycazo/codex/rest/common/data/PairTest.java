package apycazo.codex.rest.common.data;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
class PairTest {

  @Test
  void basic_operations() {
    // create basic
    Pair<String, Integer> pair = Pair.of("key", 1);
    assertThat(pair).isNotNull();
    assertThat(pair.getKey()).isEqualTo("key");
    assertThat(pair.getValue()).isEqualTo(1);
    // clone with another value
    Pair<String, Integer> pair2 = pair.withValue(2);
    assertThat(pair.getValue()).isEqualTo(1);
    assertThat(pair2.getValue()).isEqualTo(2);
    // pairs are immutable by default
    assertThat(pair.getClass().getName()).contains("ImmutablePair");
  }

  @Test
  void mutable_operations() {
    MutablePair<String, Integer> pair = Pair.ofMutable("key", 1);
    assertThat(pair).isNotNull();
    assertThat(pair.getKey()).isEqualTo("key");
    assertThat(pair.getValue()).isEqualTo(1);
    pair.setValue(2);
    assertThat(pair.getValue()).isEqualTo(2);
    // they are cloned when 'with' is used
    Pair<String, Integer> pair2 = pair.withValue(3);
    assertThat(pair2.getValue()).isEqualTo(3);
    assertThat(pair.getValue()).isEqualTo(2);
  }

  @Test
  void clone_operations() {
    MutablePair<String, Integer> p1 = Pair.ofMutable("key", 1);
    Pair<String, Integer> p2 = Pair.ofImmutable("key", 5);
    assertThat(p2.getValue()).isEqualTo(5);
    MutablePair<String, Integer> p3 = Pair.ofMutable(p1);
    ImmutablePair<String, Integer> p4 = Pair.ofImmutable(p1);
    p3.setValue(10);
    assertThat(p1.getValue()).isEqualTo(1);
    assertThat(p3.getValue()).isEqualTo(10);
    p1.setValue(2);
    assertThat(p1.getValue()).isEqualTo(2);
    assertThat(p4.getValue()).isEqualTo(1);
    Pair<String, Integer> p5 = Pair.of(p1);
    p1.setValue(0);
    assertThat(p1.getValue()).isEqualTo(0);
    assertThat(p5.getValue()).isEqualTo(2);
  }

}