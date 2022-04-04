package apycazo.codex.java.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

/**
 * Shows how a safe access operator can be imitated using java 8 Optional methods.
 */
@Slf4j
public class InstanceFieldSafeAccess {

  public static void main(String[] args) throws Exception {
    log.info(">> Safe accessor example <<");
    Element subject = Element.builder().id("a").build();
    String fallbackValue = "z";
    String value = Optional.ofNullable(subject.getElement()).map(Element::getId).orElse(fallbackValue);
    log.info("Value defaulted to '{}'", value);
    if (!fallbackValue.equals(value)) {
      throw new Exception(String.format("Expected '%s', got '%s'", fallbackValue, value));
    }
    String expectedValue = "b";
    subject.setElement(Element.builder().id(expectedValue).build());
    value = Optional.ofNullable(subject.getElement()).map(Element::getId).orElse(fallbackValue);
    log.info("Value is now '{}'", value);
    if (!expectedValue.equals(value)) {
      throw new Exception(String.format("Expected '%s', got '%s'", expectedValue, value));
    }
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
