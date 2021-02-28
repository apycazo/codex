package apycazo.codex.rest.common.security.basic;

import apycazo.codex.rest.common.security.SecurityRole;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.security.Principal;

public class BasicSecurityContext implements SecurityContext {

  private final Principal principal;
  private final boolean isSecure;
  private final String authScheme;
  private final SecurityRole securityRole;

  public BasicSecurityContext(UriInfo uriInfo, BasicCredentials credentials, SecurityRole securityRole) {
    this.principal = credentials::getUser;
    this.isSecure = uriInfo.getAbsolutePath().toString().startsWith("https");
    this.authScheme = "basic";
    this.securityRole = securityRole;
  }

  @Override
  public Principal getUserPrincipal() {
    return principal;
  }

  @Override
  public boolean isUserInRole(String role) {
    SecurityRole requiredRole = SecurityRole.valueOf(role);
    return securityRole.isRoleIncluded(requiredRole);
  }

  @Override
  public boolean isSecure() {
    return isSecure;
  }

  @Override
  public String getAuthenticationScheme() {
    return authScheme;
  }
}
