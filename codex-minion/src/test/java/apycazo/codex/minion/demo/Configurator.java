package apycazo.codex.minion.demo;

import apycazo.codex.minion.context.BeanProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

@BeanProvider
public class Configurator {

  @Singleton
  public ObjectMapper mapper() {
    return new ObjectMapper();
  }
}
