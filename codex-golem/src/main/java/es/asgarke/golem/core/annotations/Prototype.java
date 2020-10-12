package es.asgarke.golem.core.annotations;

import java.lang.annotation.*;

/**
 * Indicated the given class should be instanced, its dependencies injected, but the result is not to be registered.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Prototype {
}
