package apycazo.codex.server.dummy;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class SimpleClass implements TaggedClass {

  private final String tag = UUID.randomUUID().toString();

  public String getValue() {
    return "simple-class";
  }

  @Override
  public String getTagValue() {
    return tag;
  }
}
