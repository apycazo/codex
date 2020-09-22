package apycazo.codex.testing.spring;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig(value = {SpringComponent.class, SpringService.class})
class SpringServiceTest {

  @Autowired
  private SpringService service;

  @Test
  void test_service() {
    assertThat(service).isNotNull();
    service.addValue(1);
    service.addValue(2);
    assertThat(service.sum()).isEqualTo(3);
    service.clear();
    assertThat(service.sum()).isEqualTo(0);
  }

}