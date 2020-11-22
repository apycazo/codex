package apycazo.codex.rest.security;

public enum SecurityRole {

  ADMIN, USER;

  public boolean isRoleIncluded(SecurityRole role) {
    return this == ADMIN;
  }
}
