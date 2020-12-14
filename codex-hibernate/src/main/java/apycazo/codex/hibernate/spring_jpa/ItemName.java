package apycazo.codex.hibernate.spring_jpa;

import org.springframework.beans.factory.annotation.Value;

// Example of a simple projection.
// Ref: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections.interfaces
public interface ItemName {

  String getName();

  @Value("#{target.id + '-' + target.name}")
  String getIdWithName();
}
