package apycazo.codex.minion.demo;

import apycazo.codex.minion.context.ConfigProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

@ConfigProvider
public class Configurator {

  @Singleton
  public ObjectMapper mapper() {
    return new ObjectMapper();
  }
}
