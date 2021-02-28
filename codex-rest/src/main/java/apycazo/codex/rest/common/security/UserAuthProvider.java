package apycazo.codex.rest.common.security;

/**
 * Interface for auth provisioning. Given an user, indicates what role the user
 * has in the application. Used by the BasicSecurityManager to resolve users.
 */
public interface UserAuthProvider {

  boolean isUserPresent(String userName);
  SecurityRole getUserRole(String userName, String userPass);
}
