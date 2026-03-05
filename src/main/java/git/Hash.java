package git;

public record Hash(String hash) {

  public Hash {
    if (hash.length() != 40) {
      throw new IllegalArgumentException(
          String.format("Invalid has length. Expected 40 got: %d", hash.length()));
    }
    if (!hash.matches("[a-fA-F0-9]+")) {
      throw new IllegalArgumentException(
          String.format("Invalid hash format: %s. Expected format: %s", hash, "[a-fA-F0-9]+"));
    }
  }

  public String dirname() {
    return hash.substring(0, 2);
  }

  public String filename() {
    return hash.substring(2);
  }

  @Override
  public String toString() {
    return hash;
  }
}
