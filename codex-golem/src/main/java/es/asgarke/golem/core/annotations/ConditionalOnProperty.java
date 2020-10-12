package es.asgarke.golem.core.annotations;

import java.lang.annotation.*;

@Documented
@Repeatable(ConditionalOnProperties.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ConditionalOnProperty {

  /**
   * The expected property key to be found
   * @return the property key we expect to exist.
   */
  String value();

  /**
   * When not empty, the value we expect to find.
   * @return the value we need to return for this condition to match.
   */
  String expectedValue() default "";

  /**
   * When true, not finding the property specified as 'value', the matching we return true anyway.
   * @return true when not finding the property key should return true.
   */
  boolean matchIfMissing() default false;
}
