package es.asgarke.golem.core;

import es.asgarke.golem.core.scan.alone.LoneBean;
import es.asgarke.golem.core.scan.base.BaseConfig;
import es.asgarke.golem.core.scan.base.ExtraPackageConfig;
import es.asgarke.golem.core.scan.base.TestService;
import es.asgarke.golem.core.scan.external.ExternalBean;
import es.asgarke.golem.core.scan.extra.ExtraBean;
import es.asgarke.golem.core.scan.extra.ExtraConfig;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
public class ContextScannerTest {

  @Test
  void test_scanner() {
    ContextScanner scanner = new ContextScanner();
    scanner.scan(BaseConfig.class);
    assertThat(scanner.getConfigurationSet()).containsAll(Arrays.asList(
      ExtraConfig.class, BaseConfig.class, ExtraPackageConfig.class));
    assertThat(scanner.getSingletonSet()).containsAll(Arrays.asList(
      ExtraBean.class, LoneBean.class, TestService.class, ExternalBean.class));
    assertThat(scanner.getPackagesToScan()).containsAll(Arrays.asList(
      ExtraBean.class.getPackageName(), ExternalBean.class.getPackageName(), BaseConfig.class.getPackageName()));
  }
}
