package es.asgarke.golem.core.dummy;

import javax.inject.Named;
import javax.inject.Singleton;

import static es.asgarke.golem.core.dummy.BluePill.NAME;

@Singleton
@Named(NAME)
public class BluePill implements Pill {

  public static final String NAME = "blue-pill";

  @Override
  public boolean take() {
    return false;
  }

  @Override
  public Color color() {
    return Color.BLUE;
  }
}
