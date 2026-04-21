package git;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitTree implements GitTreeNode {

  private final GitObject gitObject;
  private final List<GitTreeNode> entries = new ArrayList<>();

  public GitTree(GitObject gitObject, List<? extends GitTreeNode> entries) {
    this.gitObject = gitObject;
    if (!gitObject.type().equals("tree")) {
      throw new RuntimeException(
          String.format("Given object must be from type tree. Found type: %s", gitObject.type()));
    }
    this.entries.addAll(entries);
  }

  public static GitTree create(Git git, List<? extends GitTreeNode> entries)
      throws NoSuchAlgorithmException, IOException {
    return GitTreeWriter.write(git, entries);
  }

  public Long size() {
    return gitObject.size();
  }

  @Override
  public List<? extends GitTreeNode> entries() {
    return Collections.unmodifiableList(entries);
  }

  public String type() {
    return gitObject.type();
  }

  @Override
  public String mode() {
    return "40000";
  }

  @Override
  public Hash hash() {
    return gitObject.hash();
  }

  public GitObject gitObject() {
    return gitObject;
  }

  @Override
  public String name() {
    return hash().toString();
  }
}
