package apycazo.codex.testing.spring;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig(value = {
  SpringServiceMockedTest.SpringMocks.class, SpringService.class
})
public class SpringServiceMockedTest {

  @Configuration
  static class SpringMocks {

    @Bean
    @Primary
    public SpringComponent springComponent() {
      return Mockito.mock(SpringComponent.class);
    }
  }

  @Autowired
  private SpringService service;
  @Autowired
  private SpringComponent component;

  @Test
  void check_mocked_call() {
    Mockito.when(component.sum()).thenReturn(10);
    assertThat(service.sum()).isEqualTo(10);
    verify(component, times(1)).sum();
  }
}
