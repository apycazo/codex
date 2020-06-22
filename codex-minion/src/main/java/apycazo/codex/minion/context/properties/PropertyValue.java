package apycazo.codex.minion.context.properties;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PropertyValue {

  String value();
}
