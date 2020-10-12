package es.asgarke.golem.types;

public interface Pair<X,Y> {

  X getLeft();
  Y getRight();
  void setLeft(X left);
  void setRight(Y right);
  Pair<X, Y> withLeft(X left);
  Pair<X, Y> withRight(Y right);

  static <X, Y> Pair<X,Y> of(X left, Y right) {
    return new MutablePair<>(left, right);
  }

  static <X, Y> Pair<X,Y> ofImmutable(X left, Y right) {
    return new ImmutablePair<>(left, right);
  }

  default Pair<X, Y> getImmutable() {
    if (this instanceof ImmutablePair) {
      return this;
    } else {
      return of(this.getLeft(), this.getRight());
    }
  }
}
