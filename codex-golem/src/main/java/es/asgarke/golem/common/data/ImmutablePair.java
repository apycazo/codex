package es.asgarke.golem.common.data;

import lombok.Getter;

@Getter
public class ImmutablePair<X,Y> implements Pair<X,Y> {

  private final X key;
  private final Y value;

  ImmutablePair(X key, Y value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public Pair<X, Y> withKey(X key) {
    return new ImmutablePair<>(key, value);
  }

  @Override
  public Pair<X, Y> withValue(Y value) {
    return new ImmutablePair<>(key, value);
  }
}
