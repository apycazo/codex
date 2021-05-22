package es.asgarke.golem.common.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MutablePair<X,Y> implements Pair<X,Y> {

  private X key;
  private Y value;

  @Override
  public Pair<X, Y> withKey(X key) {
    return new MutablePair<>(key, value);
  }

  @Override
  public Pair<X, Y> withValue(Y value) {
    return new MutablePair<>(key, value);
  }
}
