package es.asgarke.golem.core.scan.base;

import es.asgarke.golem.core.annotations.Configuration;
import es.asgarke.golem.core.scan.alone.LoneBean;
import es.asgarke.golem.core.scan.extra.ExtraConfig;

@Configuration(scanPackages = BaseConfig.class, importDefinitions = {ExtraConfig.class, LoneBean.class})
public class BaseConfig {
}
