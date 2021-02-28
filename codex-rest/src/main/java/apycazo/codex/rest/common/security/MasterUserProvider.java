package apycazo.codex.rest.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * This service is used to allow the master user, provided as a pair of property values to be resolved
 * by the basic auth service normally.
 */
@Service
public class MasterUserProvider implements UserAuthProvider {

  private final String user;
  private final String pass;

  public MasterUserProvider(
    @Value("${features.security.master.user:}") String user,
    @Value("${features.security.master.pass:}") String pass) {
    this.user = StringUtils.hasLength(user) ? user : "";
    this.pass = StringUtils.hasLength(pass) ? pass : "";
  }

  @Override
  public boolean isUserPresent(String userName) {
    return StringUtils.hasLength(user) && user.equals(userName);
  }

  @Override
  public SecurityRole getUserRole(String userName, String userPass) {
    if (user.equals(userName) && pass.equals(userPass)) {
      return SecurityRole.ADMIN;
    } else { // role = NONE means no access
      return SecurityRole.NONE;
    }
  }
}
