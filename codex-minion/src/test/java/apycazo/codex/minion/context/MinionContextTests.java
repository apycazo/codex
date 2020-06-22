package apycazo.codex.minion.context;

import apycazo.codex.minion.context.catalog.Catalog;
import apycazo.codex.minion.context.subjects.A;
import apycazo.codex.minion.context.subjects.B;
import apycazo.codex.minion.context.subjects.X;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// to review: what if we have prototype(Z implements I) ??
@Slf4j
public class MinionContextTests {

  @Test
  void testContextBasics() {
    String basePackage = MinionContextTests.class.getPackageName();
    Catalog catalog = new MinionContext(basePackage).start().getCatalog();
    // list all
    catalog.records().forEach(record -> log.info("Record: {}", record.toString()));
    A a = catalog.getByClass(A.class);
    assertThat(a).isNotNull();
    B b = catalog.getByClass(B.class);
    assertThat(b).isNotNull();
    X x = catalog.getByClass(X.class);
    assertThat(x).isNotNull();
    assertThat(x.getClassA()).isNotNull();
    assertThat(x.getClassB()).isNotNull();
    assertThat(x.getInterfaceI()).isNotNull();
    assertThat(x.getP1()).isNotNull();
    assertThat(x.getP2()).isNotNull();
    assertThat(x.getP1()).isNotEqualTo(x.getP2());
    Object bByName = catalog.getByName("i-am-b");
    assertThat(bByName).isNotNull();
    assertThat(bByName.getClass().getName()).isEqualTo(B.class.getName());
  }
}
