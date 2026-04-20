package git;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

  private final byte[] sha;

  public Hash(byte[] sha) {
    if (sha.length != 20) {
      throw new IllegalArgumentException(
          String.format("Invalid has length. Expected 20 got: %d", sha.length));
    }
    this.sha = sha;
  }

  public Hash(String sha) {
    if (sha.length() != 40) {
      throw new IllegalArgumentException(
          String.format("Invalid has length. Expected 40 got: %d", sha.length()));
    }
    if (!sha.matches("[a-fA-F0-9]+")) {
      throw new IllegalArgumentException(
          String.format("Invalid hash format: %s. Expected format: %s", sha, "[a-fA-F0-9]+"));
    }
    this.sha = toBytes(sha);

    assert sha.equals(toHex(this.sha));
  }

  private String toHex(byte[] sha) {
    StringBuilder sb = new StringBuilder();
    for (byte b : sha) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  private byte[] toBytes(String sha) {
    byte[] b = new byte[20];
    for (int i = 0; i < sha.length(); i += 2) {
      int high = Character.digit(sha.charAt(i), 16);
      int low = Character.digit(sha.charAt(i + 1), 16);
      b[i / 2] = (byte) ((high << 4) | low);
    }
    return b;
  }

  public static Hash fromContent(byte[] content) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    byte[] sha = digest.digest(content);
    return new Hash(sha);
  }

  public String hash() {
    return toHex(sha);
  }

  public byte[] bytes() {
    return sha;
  }

  public String dirname() {
    return hash().substring(0, 2);
  }

  public String filename() {
    return hash().substring(2);
  }

  @Override
  public String toString() {
    return hash();
  }
}
