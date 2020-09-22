package apycazo.codex.testing.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SpringComponent {

  private final List<Integer> values = new ArrayList<>();

  public void addValues(int... newValues) {
    if (newValues != null) {
      for (int v : newValues) {
        values.add(v);
      }
    }
  }

  public void clear() {
    values.clear();
  }

  public int count() {
    return values.size();
  }

  public int sum() {
    return values.stream().mapToInt(v -> v).sum();
  }
}
