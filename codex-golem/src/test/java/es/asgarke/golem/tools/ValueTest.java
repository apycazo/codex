package es.asgarke.golem.tools;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
class ValueTest {

  @Test
  void null_and_empty_checks_are_validated_correctly() {
    assertTrue(Value.isEmptyOrNull("  "));
    assertTrue(Value.isEmptyOrNull(null));
    assertTrue(Value.isEmptyOrNull(new Integer[]{}));
    assertTrue(Value.isEmptyOrNull(new ArrayList<Integer>()));
  }

  @Test
  void default_value_is_returned_correctly() {
    assertEquals("value", Value.orDefault("", "value"));
    assertEquals("ok", Value.orDefault("ok", "value"));
    assertEquals(100, Value.orDefault(null, 100));
    assertEquals(200, Value.orDefault(200, 100));
  }

  @Test
  void operate_over_value() {
    AtomicBoolean isOk = new AtomicBoolean(false);
    Value.ifNotEmptyOrNull("test", v -> isOk.set(true));
    assertTrue(isOk.get());
    isOk.set(false);
    Value.ifNotEmptyOrNull("", v -> isOk.set(true));
    assertFalse(isOk.get());
    Value.ifNotEmptyOrNull(new Integer[]{1}, v -> isOk.set(true));
    assertTrue(isOk.get());
    isOk.set(false);
    Value.ifNotEmptyOrNull(new Integer[]{}, v -> isOk.set(true));
    assertFalse(isOk.get());
    Value.ifNotEmptyOrNull(Arrays.asList(1,2,3), v -> isOk.set(true));
    assertTrue(isOk.get());
    isOk.set(false);
    Value.ifNotEmptyOrNull(Collections.emptyList(), v -> isOk.set(true));
    assertFalse(isOk.get());
  }
}