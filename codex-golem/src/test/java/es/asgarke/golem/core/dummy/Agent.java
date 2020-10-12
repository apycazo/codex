package es.asgarke.golem.core.dummy;

import es.asgarke.golem.core.annotations.Prototype;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
@Prototype
public class Agent {

  @Getter
  private final String id = UUID.randomUUID().toString();
  @Getter
  private boolean visible = false;

  @PostConstruct
  private void wakeUp() {
    visible = true;
  }
}
