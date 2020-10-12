package es.asgarke.golem.core.annotations;

import java.lang.annotation.*;

/**
 * Indicates the bean to inject might not be available, but it should not produce an error.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface NonRequired {
}
