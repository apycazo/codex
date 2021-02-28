package apycazo.codex.rest.common.security.basic;

import apycazo.codex.rest.common.security.SecurityManager;
import apycazo.codex.rest.common.security.SecurityRole;
import apycazo.codex.rest.common.security.UserAuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BasicSecurityManager implements SecurityManager {

  private final List<UserAuthProvider> authProviders;

  public BasicSecurityManager(ObjectProvider<List<UserAuthProvider>> authProviders) {
    this.authProviders = authProviders.getIfAvailable(Collections::emptyList);
  }

  @Override
  public boolean authenticate(ContainerRequestContext requestContext) {
    SecurityContext securityContext;
    if (!authProviders.isEmpty()) {
      String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
      BasicCredentials credentials = BasicCredentials.fromHeader(authorization);
      String userName = credentials.getUser();
      Optional<UserAuthProvider> provider = authProviders.stream()
        .filter(ap -> ap.isUserPresent(userName))
        .findFirst();
      if (provider.isPresent()) {
        UriInfo uriInfo = requestContext.getUriInfo();
        SecurityRole role = provider.get().getUserRole(userName, credentials.getPassword());
        if (role != SecurityRole.NONE) {
          securityContext = new BasicSecurityContext(uriInfo, credentials, role);
          requestContext.setSecurityContext(securityContext);
          return true;
        }
      }
    }
    return false;
  }
}
