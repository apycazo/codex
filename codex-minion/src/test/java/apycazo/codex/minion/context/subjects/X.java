package apycazo.codex.minion.context.subjects;

import lombok.Data;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Data
@Singleton
public class X {

  @Inject
  @Named("i-am-b")
  private I interfaceI;
  @Inject
  private A classA;
  @Inject
  private B classB;
  @Inject
  private P p1, p2;
}
