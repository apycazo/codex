package apycazo.codex.testing.vanilla;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class VanillaService {

  private final List<Integer> values = new ArrayList<>();

  public VanillaService addValue(int value) {
    values.add(value);
    return this;
  }

  public VanillaService addValues(int... newValues) {
    for (int value : newValues) values.add(value);
    return this;
  }

  public VanillaService addValueIfNotPresent(int value) {
    if (!values.contains(value)) {
      values.add(value);
    }
    return this;
  }

  public VanillaService removeValue(int value) {
    values.remove(value);
    return this;
  }

  public Stream<Integer> getValues() {
    return values.stream();
  }

  public boolean containsValue(int value) {
    return values.contains(value);
  }

  public VanillaService clear() {
    values.clear();
    return this;
  }

  public int count() {
    return values.size();
  }

  public int sum() {
    return values.stream().mapToInt(v -> v).sum();
  }

}
