package apycazo.codex.testing.vanilla;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(JUnitPlatform.class)
class VanillaServiceTest {

  private static VanillaService service;

  @BeforeAll
  static void setupTest() {
    service = new VanillaService();
  }

  @BeforeEach
  void setupScenario() {
    service.addValues(1, 2, 3, 4, 5);
  }

  @AfterEach
  void cleanupScenario() {
    service.clear();
  }

  @Test
  void basic_behavior() {
    assertThat(service.count()).isEqualTo(3);
    service.addValues(6, 7, 8);
    assertThat(service.count()).isEqualTo(8);
    assertThat(service.containsValue(1)).isTrue();
    assertThat(service.containsValue(8)).isTrue();
    log.info("Basic behavior OK");
  }

}