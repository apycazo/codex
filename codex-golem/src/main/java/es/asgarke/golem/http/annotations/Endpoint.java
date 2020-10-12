package es.asgarke.golem.http.annotations;

import es.asgarke.golem.http.definitions.HttpMethod;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Endpoint {

  HttpMethod method() default HttpMethod.GET;

  String path() default "";

  String produces() default "";

  String consumes() default "";
}
