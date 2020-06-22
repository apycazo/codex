package apycazo.codex.minion.context.conditions;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface OnClassCondition {

  String className();
}
