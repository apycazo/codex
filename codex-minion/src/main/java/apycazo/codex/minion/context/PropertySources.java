package apycazo.codex.minion.context;

import java.lang.annotation.*;

/**
 * Declares additional property source locations. This annotation is only considered when found
 * on classes annotated with 'ConfigProvider'.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PropertySources {

  PropertySource [] value();
}
