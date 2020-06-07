package apycazo.codex.server;

import apycazo.codex.server.catalog.Catalog;
import apycazo.codex.server.dummy.ComposedClass;
import apycazo.codex.server.dummy.PrototypeClass;
import apycazo.codex.server.dummy.SimpleClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CatalogTests {

  @Test
  void testCatalogBasic() {
    Catalog catalog = new Catalog();
    SimpleClass instance = catalog.singleton(SimpleClass.class);
    assertThat(instance).isNotNull();
    assertThat(instance.getValue()).isEqualTo("simple-class");
  }

  @Test
  void testCatalogComposed() {
    Catalog catalog = new Catalog();
    ComposedClass instance = catalog.singleton(ComposedClass.class);
    assertThat(instance).isNotNull();
    assertThat(instance.getValue()).isEqualTo("simple-class");
  }

  @Test
  void testCatalogCache() {
    Catalog catalog = new Catalog();
    SimpleClass simple1 = catalog.singleton(SimpleClass.class);
    SimpleClass simple2 = catalog.singleton(SimpleClass.class);
    // both instances are the same
    assertThat(simple1.getTagValue()).isEqualTo(simple2.getTagValue());
    ComposedClass composed = catalog.singleton(ComposedClass.class);
    assertThat(composed).isNotNull();
    assertThat(composed.getValue()).isEqualTo("simple-class");
    // the inner instance is also the same we had created previously
    assertThat(composed.getInnerInstance().getTagValue()).isEqualTo(simple1.getTagValue());
    // check that the prototyped instance is different
    assertThat(composed.getPrototyped().getTagValue()).isNotEqualTo(simple1.getTagValue());
    PrototypeClass prototypeClass = catalog.singleton(PrototypeClass.class);
    assertThat(prototypeClass.getTagValue()).isNotEqualTo(composed.getPrototypeClass().getTagValue());
  }

  @Test
  void testCatalogNames() {

  }

}
