package apycazo.codex.rest.common.security;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
  SecurityRole[] rolesAllowed() default {};
}


