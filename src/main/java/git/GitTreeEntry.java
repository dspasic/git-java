package git;

import java.util.List;
import java.util.Objects;

public class GitTreeEntry implements GitTreeNode {

  private final String mode;
  private final String name;
  private final Hash hash;

  public GitTreeEntry(String mode, String name, byte[] hash) {
    this.mode = Objects.requireNonNull(mode, "mode must not be null.");
    this.name = Objects.requireNonNull(name, "name must not be null.");
    this.hash = new Hash(Objects.requireNonNull(hash, "hash must not be null."));
  }

  @Override
  public String mode() {
    return mode;
  }

  @Override
  public List<GitTreeNode> entries() {
    return List.of();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Hash hash() {
    return hash;
  }
}
