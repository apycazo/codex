package es.asgarke.golem.core.scan;

import es.asgarke.golem.core.BeanFactory;
import es.asgarke.golem.core.GolemContext;
import es.asgarke.golem.core.scan.base.BaseConfig;
import es.asgarke.golem.core.scan.base.TestService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
public class ScanTest {

  private static GolemContext context;

  @BeforeAll
  static void setup() {
    context = GolemContext.startContext(BaseConfig.class);
  }

  @Test
  void test_imports() {
    assertThat(context).isNotNull();
    BeanFactory factory = context.getFactory();
    assertThat(factory).isNotNull();
    Optional<TestService> testService = factory.resolveBean(TestService.class);
    assertThat(testService.isPresent()).isTrue();
    TestService service = testService.get();
    assertThat(service.getExtraBean()).isNotNull();
    assertThat(service.getLoneBean()).isNotNull();
    assertThat(service.getExternalBean()).isNotNull();
    assertThat(service.getExcludedBean()).isNull();
  }
}
