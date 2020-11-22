package apycazo.codex.rest.security.basic;

import apycazo.codex.rest.security.SecurityRole;
import apycazo.codex.rest.security.SecurityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

@Slf4j
@Service
public class BasicSecurityManager implements SecurityManager {

  private final String masterUserName;
  private final String masterUserPass;

  public BasicSecurityManager(
    @Value("${features.security.master.user:}") String user,
    @Value("${features.security.master.pass:}") String pass) {
    this.masterUserName = user;
    this.masterUserPass = pass;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public boolean authenticate(ContainerRequestContext requestContext) {
    if (!StringUtils.isEmpty(masterUserName) &&!StringUtils.isEmpty(masterUserPass)) {
      String authentication = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
      BasicCredentials credentials = BasicCredentials.fromHeader(authentication);
      if (masterUserName.equals(credentials.getUser()) &&
          masterUserPass.equals(credentials.getPassword())) {
        UriInfo uriInfo = requestContext.getUriInfo();
        SecurityContext securityContext = new BasicSecurityContext(uriInfo, credentials, SecurityRole.ADMIN);
        requestContext.setSecurityContext(securityContext);
        return true;
      }
    }
    return false;
  }
}
