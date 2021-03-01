package apycazo.codex.java.security;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
class PublicPrivateKeysTest {

  @Test
  void process_text() throws Exception {
    final String original = "lorem ipsum";
    final PublicPrivateKeys service = new PublicPrivateKeys();
    String encrypted = service.encryptString(original);
    assertNotEquals(original, encrypted);
    System.out.println("Encrypted text: " + encrypted);
    String decrypted = service.decryptString(encrypted);
    assertEquals(original, decrypted);
  }
}