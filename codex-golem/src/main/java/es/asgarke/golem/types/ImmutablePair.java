package es.asgarke.golem.types;

public class ImmutablePair <X, Y> implements Pair<X, Y> {

  private final X left;
  private final Y right;

  ImmutablePair(X left, Y right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public X getLeft() {
    return left;
  }

  @Override
  public Y getRight() {
    return right;
  }

  @Override
  public void setLeft(X value) {
    throw new UnsupportedOperationException("Instance is immutable");
  }

  @Override
  public void setRight(Y value) {
    throw new UnsupportedOperationException("Instance is immutable");
  }

  @Override
  public Pair<X, Y> withLeft(X left) {
    return Pair.ofImmutable(left, getRight());
  }

  @Override
  public Pair<X, Y> withRight(Y right) {
    return Pair.ofImmutable(getLeft(), right);
  }

  @Override
  public String toString() {
    return String.format("Pair{%s,%s}", left, right);
  }
}
