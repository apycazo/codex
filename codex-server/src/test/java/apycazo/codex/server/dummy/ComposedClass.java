package apycazo.codex.server.dummy;

import apycazo.codex.server.annotations.Prototype;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class ComposedClass implements TaggedClass {

  private final String tag = UUID.randomUUID().toString();

  @Inject
  private SimpleClass simpleClass;
  @Inject
  @Prototype
  private SimpleClass prototyped;
  @Inject
  private PrototypeClass prototypeClass;

  public String getValue() {
    return Optional.ofNullable(simpleClass).map(SimpleClass::getValue).orElse("Empty!");
  }

  public SimpleClass getInnerInstance() {
    return simpleClass;
  }

  public SimpleClass getPrototyped() {
    return prototyped;
  }

  public PrototypeClass getPrototypeClass() {
    return prototypeClass;
  }

  @Override
  public String getTagValue() {
    return tag;
  }
}
