package es.asgarke.golem.types;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MutablePair<X, Y> implements Pair<X, Y> {

  private X left;
  private Y right;

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
    this.left = value;
  }

  @Override
  public void setRight(Y value) {
    this.right = value;
  }

  @Override
  public Pair<X, Y> withLeft(X left) {
    setLeft(left);
    return this;
  }

  @Override
  public Pair<X, Y> withRight(Y right) {
    setRight(right);
    return this;
  }

  @Override
  public String toString() {
    return String.format("Pair{%s,%s}", left, right);
  }
}
