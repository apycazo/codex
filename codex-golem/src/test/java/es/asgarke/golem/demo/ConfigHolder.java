package es.asgarke.golem.demo;

import es.asgarke.golem.core.annotations.PropertyValue;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Getter
@Singleton
public class ConfigHolder {

  private final String appName;
  private final String path;
  private final int port;
  private final int poolSize;

  @Inject
  public ConfigHolder(
    @PropertyValue("application.name") String appName,
    @PropertyValue("golem.server.mapping.base") String path,
    @PropertyValue("golem.server.http.port") int port,
    @PropertyValue("golem.server.pool.size") int poolSize) {
    this.appName = appName;
    this.path = path;
    this.port = port;
    this.poolSize = poolSize;
  }
}
