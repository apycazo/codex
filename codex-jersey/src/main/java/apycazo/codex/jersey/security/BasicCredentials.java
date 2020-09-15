package apycazo.codex.jersey.security;

import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
public class BasicCredentials {

  private String user, password;
  private static final String ANONYMOUS = "anonymous";

  public BasicCredentials(String user, String password) {
    this.user = user;
    this.password = password;
  }

  public boolean isAnonymous() {
    return ANONYMOUS.equals(user);
  }

  public static BasicCredentials anonymous() {
    return new BasicCredentials(ANONYMOUS, "");
  }

  public static BasicCredentials fromHeader(String hdr) {
    if (hdr == null || !hdr.toLowerCase().startsWith("basic")) {
      return BasicCredentials.anonymous();
    } else {
      String base64Credentials = hdr.substring("Basic".length()).trim();
      byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
      String credentials = new String(credDecoded, StandardCharsets.UTF_8);
      // credentials = username:password
      final String[] values = credentials.split(":", 2);
      if (values.length != 2) return BasicCredentials.anonymous();
      else return new BasicCredentials(values[0], values[1]);
    }
  }
}
