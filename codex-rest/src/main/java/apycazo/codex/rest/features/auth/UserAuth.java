package apycazo.codex.rest.features.auth;

import apycazo.codex.rest.common.data.Pair;
import apycazo.codex.rest.common.security.SecurityRole;
import apycazo.codex.rest.common.security.UserAuthProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * This service emulates what a login service should provide. In a more realistic context, this should
 * query a database or other service to validate the user/password pairs.
 */
@Service
public class UserAuth implements UserAuthProvider {

  private final Map<String, Pair<String, SecurityRole>> users;

  public UserAuth() {
    users = new HashMap<>();
    users.put("john", Pair.of("secret", SecurityRole.USER));
    users.put("jane", Pair.of("s3cr3t", SecurityRole.USER));
  }

  @Override
  public boolean isUserPresent(String userName) {
    return users.containsKey(userName);
  }

  @Override
  public SecurityRole getUserRole(String userName, String userPass) {
    if (isUserPresent(userName)) {
      Pair<String, SecurityRole> config = users.get(userName);
      return config.getKey().equals(userPass) ? config.getValue() : SecurityRole.NONE;
    } else {
      return SecurityRole.NONE;
    }
  }
}
