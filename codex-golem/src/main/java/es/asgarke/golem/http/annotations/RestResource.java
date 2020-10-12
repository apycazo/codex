package es.asgarke.golem.http.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestResource {

  String path() default "";

  String produces() default "";

  String consumes() default "";
}
