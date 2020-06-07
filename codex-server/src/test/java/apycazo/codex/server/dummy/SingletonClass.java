package apycazo.codex.server.dummy;

import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class SingletonClass implements TaggedClass {

  private final String tag = UUID.randomUUID().toString();

  @Override
  public String getTagValue() {
    return tag;
  }
}
