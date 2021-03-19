package apycazo.codex.rest.common.util;

import java.util.UUID;

/**
 * Ensures IDs generated are consistent through the application.
 */
public class IdGenerator {

  public static String generateId() {
    return UUID.randomUUID().toString().substring(24);
  }
}
