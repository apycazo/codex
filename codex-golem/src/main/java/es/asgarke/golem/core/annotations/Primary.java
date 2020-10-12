package es.asgarke.golem.core.annotations;

import java.lang.annotation.*;

/**
 * Declares a @Singleton bean to be the primary one when more than one can be used.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Primary {
}
