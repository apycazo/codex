package es.asgarke.golem.core.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ConditionalOnProperties {

  ConditionalOnProperty[] value() default {};
}
