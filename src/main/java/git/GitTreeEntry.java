package git;

import java.util.List;
import java.util.Objects;

public class GitTreeEntry implements TreeNode {

  private final String mode;
  private final String name;
  private final byte[] hash;

  GitTreeEntry(String mode, String name, byte[] hash) {
    this.mode = Objects.requireNonNull(mode, "mode must not be null.");
    this.name = Objects.requireNonNull(name, "name must not be null.");
    this.hash = Objects.requireNonNull(hash, "hash must not be null.");
  }

  public String type() {
    return mode;
  }

  @Override
  public List<TreeNode> entries() {
    return List.of();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String hash() {
    return new String(hash);
  }
}
