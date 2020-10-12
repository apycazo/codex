package es.asgarke.golem.core;

import es.asgarke.golem.core.dummy.MatrixService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
public class CoreFunctionTest {

  private static GolemContext context;
  private static MatrixService dummy;

  @BeforeAll
  static void setup() {
    context = GolemContext.startContext(MatrixService.class);
    try {
      dummy = context.getFactory().resolveBean(MatrixService.class).orElse(null);
    } catch (Exception e) {
      dummy = null;
    }
  }

  @Test
  void test_context_starts_ok() {
    assertThat(context).isNotNull();
  }

  @Test
  void test_dummy_is_injected() {
    assertThat(context.getFactory().resolveBean(MatrixService.class).isPresent()).isTrue();
  }

  @Test
  void test_field_injection() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getBlue()).isNotNull();
  }

  @Test
  void test_named_field_injection() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getOptionOne()).isNotNull();
  }

  @Test
  void test_constructor_injection() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getRed()).isNotNull();
  }

  @Test
  void test_named_constructor_injection() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getOptionTwo()).isNotNull();
  }

  @Test
  void test_existing_property_injection() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getInitiateName()).isEqualTo("neo");
    assertThat(dummy.getMasterName()).isEqualTo("morpheus");
  }

  @Test
  void test_default_property_injection() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getAgentName()).isEqualTo("unknown");
  }

  @Test
  void test_constructor_property_injection() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getAppName()).isEqualTo("golem-test");
  }

  @Test
  void test_prototype_injection() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getAsh()).isNotNull();
    assertThat(dummy.getSmith()).isNotNull();
    assertThat(dummy.getSmith().getId().equals(dummy.getAsh().getId())).isFalse();
  }

  @Test
  void test_post_construct() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getAsh()).isNotNull();
    assertThat(dummy.getAsh().isVisible()).isTrue();
    assertThat(dummy.getSmith()).isNotNull();
    assertThat(dummy.getSmith().isVisible()).isTrue();
  }

  @Test
  void test_non_required_bean_is_missing() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getBlackCat()).isNull();
  }

  @Test
  void test_context_in_injected() {
    assertThat(dummy).isNotNull();
    assertThat(dummy.getContext()).isNotNull();
  }
}
