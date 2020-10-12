package es.asgarke.golem.core.dummy;

public interface Pill {

  enum Color {RED, BLUE, GREEN}

  Color color();
  boolean take();
}
