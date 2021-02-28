package apycazo.codex.rest.common.security;

public enum SecurityRole {

  NONE, ADMIN, USER;

  /**
   * Returns if the current role can be provided from the given param.
   * In this regard, and admin role includes all other roles. For example, when a USER role wants to access
   * an endpoint requiring ADMIN, the param will contain ADMIN.
   * @param role the role we want to match.
   * @return true is the role required is allowed to continue with this one.
   */
  public boolean isRoleIncluded(SecurityRole role) {
    if (this == ADMIN) {
      return true;
    } else {
      return role == this;
    }
  }
}
