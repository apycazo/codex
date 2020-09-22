package apycazo.codex.testing.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SpringService {

  private final SpringComponent component;

  public SpringService(SpringComponent component) {
    this.component = component;
  }

  public void init() {
    component.addValues(1,2,3,4,5);
  }

  public int sum() {
    return component.sum();
  }

  public void addValue(int v) {
    component.addValues(v);
  }

  public void clear() {
    component.clear();
  }

}
