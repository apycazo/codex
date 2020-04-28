package apycazo.codex.java.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shows how a safe access operator can be imitated using java 8 Optional methods.
 */
@Slf4j
public class InstanceFieldSafeAccess {

  public static void main(String[] args) {
    log.info(">> Safe accessor example <<");
    Element subject = Element.builder().id("a").build();
    String fallbackValue = "z";
    String value = Optional.ofNullable(subject.getElement()).map(Element::getId).orElse(fallbackValue);
    log.info("Value defaulted to '{}'", value);
    assertThat(value).isEqualTo(fallbackValue);
    String expectedValue = "b";
    subject.setElement(Element.builder().id(expectedValue).build());
    value = Optional.ofNullable(subject.getElement()).map(Element::getId).orElse(fallbackValue);
    log.info("Value is now '{}'", value);
    assertThat(value).isEqualTo(expectedValue);
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  private static class Element {
    @Builder.Default
    private String id = UUID.randomUUID().toString().substring(24);
    @Builder.Default
    private Element element = null;
  }

}
