package apycazo.codex.minion.context;

import java.lang.annotation.*;

/**
 * Declares an additional property source location. This annotation is only considered when found
 * on classes annotated with 'ConfigProvider'.
 */
@Documented
@Repeatable(PropertySources.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PropertySource {

  String location();
  boolean mandatory() default false;
}
