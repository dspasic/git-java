package git;

import git.command.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitTree implements TreeNode {

  private final GitObject gitObject;
  private final List<GitTreeEntry> entries = new ArrayList<>();

  public GitTree(GitObject gitObject, List<GitTreeEntry> entries) {
    this.gitObject = gitObject;
    if (!gitObject.type().equals("tree")) {
      throw new RuntimeException(
          String.format("Given object must be from type tree. Found type: %s", gitObject.type()));
    }
    this.entries.addAll(entries);
  }

  public Long size() {
    return gitObject.size();
  }

  public List<GitTreeEntry> entries() {
    return Collections.unmodifiableList(entries);
  }

  @Override
  public String type() {
    return gitObject.type();
  }
}
