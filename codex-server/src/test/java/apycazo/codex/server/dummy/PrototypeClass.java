package apycazo.codex.server.dummy;

import apycazo.codex.server.annotations.Prototype;

import java.util.UUID;

@Prototype
public class PrototypeClass implements TaggedClass {

  private String tag = UUID.randomUUID().toString();

  @Override
  public String getTagValue() {
    return tag;
  }
}
