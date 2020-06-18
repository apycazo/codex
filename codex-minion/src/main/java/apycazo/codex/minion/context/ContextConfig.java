package apycazo.codex.minion.context;

import apycazo.codex.minion.common.CoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static apycazo.codex.minion.common.StatusCode.BASE_PACKAGE_NULL;

public class ContextConfig {

  private String[] basePackageNames;
  private MinionContext minionContext = null;
  private final Set<String> propertySources;

  private ContextConfig() {
    propertySources = Stream
      .of("classpath:application.properties", "file:application.properties")
      .collect(Collectors.toSet());
  }

  public static ContextConfig basePackages(String ... packageNames) {
    ContextConfig ctx = new ContextConfig();
    if (packageNames == null || packageNames.length == 0) {
      throw new CoreException(BASE_PACKAGE_NULL);
    } else {
      ctx.basePackageNames = packageNames;
    }
    return ctx;
  }

  public ContextConfig withPropertySources(String ... sources) {
    if (sources != null && sources.length > 0) {
      propertySources.addAll(Arrays.asList(sources));
    }
    return this;
  }

  public MinionContext start() {
    minionContext = new MinionContext(propertySources, basePackageNames);
    return minionContext;
  }

  public Optional<MinionContext> getContext() {
    return Optional.ofNullable(minionContext);
  }
}
