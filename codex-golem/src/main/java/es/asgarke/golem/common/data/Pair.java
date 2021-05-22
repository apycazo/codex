package es.asgarke.golem.common.data;

public interface Pair<X,Y> {

  X getKey();
  Y getValue();
  Pair<X,Y> withKey(X key);
  Pair<X,Y> withValue(Y value);

  static <X,Y> Pair<X,Y> of(X key, Y value) {
    return new ImmutablePair<>(key, value);
  }

  static <X,Y> Pair<X,Y> of(Pair<X,Y> pair) {
    return new ImmutablePair<>(pair.getKey(), pair.getValue());
  }

  static <X,Y> ImmutablePair<X,Y> ofImmutable(X key, Y value) {
    return new ImmutablePair<>(key, value);
  }

  static <X,Y> ImmutablePair<X,Y> ofImmutable(Pair<X,Y> pair) {
    return new ImmutablePair<>(pair.getKey(), pair.getValue());
  }

  static <X,Y> MutablePair<X,Y> ofMutable(X key, Y value) {
    return new MutablePair<>(key, value);
  }

  static <X,Y> MutablePair<X,Y> ofMutable(Pair<X,Y> pair) {
    return new MutablePair<>(pair.getKey(), pair.getValue());
  }
}
