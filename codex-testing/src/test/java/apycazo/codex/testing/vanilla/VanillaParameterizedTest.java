package apycazo.codex.testing.vanilla;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Requires junit-jupiter-params, do not play well with mixed @Test cases.
 */
@Slf4j
@RunWith(JUnitPlatform.class)
class VanillaParameterizedTest {

  private static final VanillaService service = new VanillaService();

  @BeforeEach
  void cleanup() {
    service.clear().addValues(1, 2, 3, 4, 5);
  }

  @ParameterizedTest
  @ValueSource(ints = {8,9})
  void test_param_values(int input) {
    assertThat(service.containsValue(input)).isFalse();
    service.addValue(input);
    assertThat(service.containsValue(input)).isTrue();
    log.info("test param value: {}", input);
  }

  @ParameterizedTest
  @CsvSource(value = {"10,25", "5,20"})
  void test_param_csv_values(String valueStr, String expectedSumStr) {
    int value = Integer.parseInt(valueStr);
    int expected = Integer.parseInt(expectedSumStr);
    assertThat(service.addValue(value).sum()).isEqualTo(expected);
    log.info("test param csv value: {},{}", valueStr, expectedSumStr);
  }

}
