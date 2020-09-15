package apycazo.codex.jersey.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SecurityService {

  private final Map<String, String> authorizedCredentials;

  public SecurityService() {
    authorizedCredentials = new HashMap<>();
    // register demo user
    authorizedCredentials.put("demo", "mysecretpwd");
  }

  public boolean validateCredentials(BasicCredentials basicCredentials) {
    return validateCredentials(
      basicCredentials.getUser(),
      basicCredentials.getPassword());
  }

  public boolean validateCredentials(String user, String pwd) {
    return Optional.ofNullable(authorizedCredentials.get(user))
      .map(userPass -> userPass.equals(pwd))
      .orElse(false);
  }
}
