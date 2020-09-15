package apycazo.codex.feign;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserPwd {

  private final String user;
  private final String pwd;
}
