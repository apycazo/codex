package es.asgarke.golem.core.scan.base;

import es.asgarke.golem.core.annotations.NonRequired;
import es.asgarke.golem.core.scan.alone.ExcludedBean;
import es.asgarke.golem.core.scan.alone.LoneBean;
import es.asgarke.golem.core.scan.external.ExternalBean;
import es.asgarke.golem.core.scan.extra.ExtraBean;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class TestService {

  @Inject
  private ExtraBean extraBean;
  @Inject
  private LoneBean loneBean;
  @Inject
  private ExternalBean externalBean;
  @Inject
  @NonRequired
  private ExcludedBean excludedBean;
}
