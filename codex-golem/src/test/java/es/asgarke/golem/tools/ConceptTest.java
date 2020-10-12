package es.asgarke.golem.tools;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
public class ConceptTest {

  @Test
  void test_class_set() {
    Set<Class<?>> set = new HashSet<>();
    set.add(String.class);
    set.add(String.class);
    set.add(Long.class);
    assertThat(set.size()).isEqualTo(2);
    assertThat(set.contains(String.class)).isTrue();
    assertThat(set.contains(Long.class)).isTrue();
  }

}
