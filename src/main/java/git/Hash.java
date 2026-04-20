package git;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

  private final byte[] hashBytes;

  public Hash(byte[] hashBytes) {
    if (hashBytes.length != 20) {
      throw new IllegalArgumentException(
          String.format("Invalid has length. Expected 20 got: %d", hashBytes.length));
    }
    this.hashBytes = hashBytes;
  }

  public Hash(String hash) {
    if (hash.length() != 40) {
      throw new IllegalArgumentException(
          String.format("Invalid has length. Expected 40 got: %d", hash.length()));
    }
    if (!hash.matches("[a-fA-F0-9]+")) {
      throw new IllegalArgumentException(
          String.format("Invalid hash format: %s. Expected format: %s", hash, "[a-fA-F0-9]+"));
    }
    this.hashBytes = toBytes(hash);

    assert hash.equals(toHex(hashBytes));
  }

  private String toHex(byte[] hashBytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : hashBytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  private byte[] toBytes(String hash) {
    byte[] b = new byte[20];
    for (int i = 0; i < hash.length(); i += 2) {
      int high = Character.digit(hash.charAt(i), 16);
      int low = Character.digit(hash.charAt(i + 1), 16);
      b[i / 2] = (byte) ((high << 4) | low);
    }
    return b;
  }

  public static Hash fromContent(byte[] content) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    byte[] hash = digest.digest(content);
    return new Hash(hash);
  }

  public String hash() {
    return toHex(hashBytes);
  }

  public byte[] bytes() {
    return hashBytes;
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
