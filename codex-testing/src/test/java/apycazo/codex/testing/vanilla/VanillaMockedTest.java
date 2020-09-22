package apycazo.codex.testing.vanilla;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class VanillaMockedTest {

  @Mock // replaces Mockito.mock(..)
  private VanillaService service;

  @Test // the value injected here is not the same as the 'service' above.
  void test_injected_mocked_service(@Mock VanillaService altService) {
    Mockito.when(altService.sum()).thenReturn(5);
    assertThat(altService.sum()).isEqualTo(5);
    assertThat(altService).isNotEqualTo(service);
  }

  @Test
  void test_mocked_service() {
    Mockito.when(service.sum()).thenReturn(10);
    assertThat(service.sum()).isEqualTo(10);
    Mockito.verify(service).sum();
  }
}
