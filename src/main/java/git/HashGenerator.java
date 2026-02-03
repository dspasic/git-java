package git;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {

  public static Hash generateHash(byte[] data) throws NoSuchAlgorithmException, IOException {
    var digest = MessageDigest.getInstance("SHA-1");
    byte[] hashBytes = digest.digest(data);
    StringBuilder sb = new StringBuilder();
    for (byte b : hashBytes) {
      sb.append(String.format("%02x", b));
    }
    return new Hash(sb.toString());
  }
}
