package apycazo.codex.java.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Configuration
public class SpringObjectProvider {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringObjectProvider.class);
    ctx.refresh();
  }

  @Component
  public static class ElementA implements Element {}

  @Component
  public static class ElementB implements Element {}

  public static class ElementC implements Element {}

  @Service
  public static class ClientService {

    public ClientService(
      List<Element> allElements, // this should contain 2 elements: A & B
      List<Feature> allFeatures, // this should be empty, but not null
      ObjectProvider<List<Element>> elementListProvider, // same as all elements!
      ObjectProvider<List<Feature>> featureListProvider, // this is not available,
      //ObjectProvider<Element> singleElementInterface, // fails, not an unique bean
      ObjectProvider<ElementA> singleElementInstance
    ) {
      log.info("allElements: {} ({})", allElements != null, allElements != null ? allElements.size() : -1);
      log.info("allFeatures: {} ({})", allFeatures != null, allFeatures != null ? allFeatures.size() : -1);
      elementListProvider.ifAvailable(elements -> log.info("elementListProvider had {} entries", elements.size()));
      featureListProvider.ifAvailable(features -> log.info("featureListProvider had {} entries", features.size()));
      singleElementInstance.ifAvailable(el -> log.info("Class: {}", el.getClass().getName()));
    }
  }

  public interface Element {
    // empty
  }

  public interface Feature {
    // empty
  }
}
