package git;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {

  public static Hash generateHash(byte[] data) throws NoSuchAlgorithmException {
    var digest = MessageDigest.getInstance("SHA-1");
    byte[] hashBytes = digest.digest(data);
    return toHex(hashBytes);
  }

  public static Hash toHex(byte[] hash) {
    StringBuilder sb = new StringBuilder();
    for (byte b : hash) {
      sb.append(String.format("%02x", b));
    }
    return new Hash(sb.toString());
  }
}
