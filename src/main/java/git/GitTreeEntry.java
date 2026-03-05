package git;

import git.command.TreeNode;

public record GitTreeEntry(String mode, String name, String sha) implements TreeNode {

  public String type() {
    return mode;
  }
}
