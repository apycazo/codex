package es.asgarke.golem.core.dummy;

import javax.inject.Named;
import javax.inject.Singleton;

import static es.asgarke.golem.core.dummy.RedPill.NAME;

@Singleton
@Named(NAME)
public class RedPill implements Pill {

  public static final String NAME = "red-pill";

  @Override
  public boolean take() {
    return true;
  }

  @Override
  public Color color() {
    return Color.RED;
  }
}
