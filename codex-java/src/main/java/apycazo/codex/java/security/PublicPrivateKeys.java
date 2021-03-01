package apycazo.codex.java.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class PublicPrivateKeys {

  private final KeyPair keyPair;
  private final Cipher rsaCipher;

  public PublicPrivateKeys() throws NoSuchAlgorithmException, NoSuchPaddingException {
    rsaCipher = Cipher.getInstance("RSA");
    KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
    rsa.initialize(512);
    keyPair = rsa.generateKeyPair();
  }

  public String encryptString(String string)
    throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    PublicKey publicKey = keyPair.getPublic();
    rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] utf8content = string.getBytes(StandardCharsets.UTF_8);
    // Encrypts or decrypts data in a single-part operation, or finishes a multiple-part operation
    byte[] bytes = rsaCipher.doFinal(utf8content);
    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(bytes);
  }

  public String decryptString(String string)
    throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    PrivateKey privateKey = keyPair.getPrivate();
    rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] content = decoder.decode(string);
    return new String(rsaCipher.doFinal(content), StandardCharsets.UTF_8);
  }

}
