package apycazo.codex.jersey.security;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.security.Principal;

public class ApiSecurityContext implements SecurityContext {

  private final Principal principal;
  private final boolean isSecure;
  private final String authScheme;

  public ApiSecurityContext(UriInfo uriInfo, BasicCredentials credentials) {
    principal = credentials::getUser;
    isSecure = uriInfo.getAbsolutePath().toString().startsWith("https");
    authScheme = "basic";
  }

  @Override
  public Principal getUserPrincipal() {
    return principal;
  }

  @Override
  public boolean isUserInRole(String role) {
    return true;
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
