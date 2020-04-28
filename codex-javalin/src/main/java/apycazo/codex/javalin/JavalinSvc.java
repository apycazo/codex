package apycazo.codex.javalin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JavalinSvc {

  private final List<String> data = Arrays.asList("john", "dana", "fox");

  public int count() {
    return data.size();
  }

  public String value(int id) {
    if (id < 0 || id >= data.size()) {
      return null;
    } else {
      return data.get(id);
    }
  }

  public Stream<String> all() {
    return data.stream();
  }
}
