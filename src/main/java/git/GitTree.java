package git;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

  public static GitTree create(List<? extends GitTreeNode> entries) {
    if (entries.isEmpty()) {
      return null;
    }

    entries.sort(Comparator.comparing(GitTreeNode::name));

    StringBuilder sb = new StringBuilder();

    for (GitTreeNode e : entries) {
      System.out.println(e);
      sb.append("%s %s\u0000%s".formatted(e.type(), e.name(), e.hash()));
    }

    System.out.println(sb.toString());

    return null;
  }

  public Long size() {
    return gitObject.size();
  }

  @Override
  public List<? extends GitTreeNode> entries() {
    return Collections.unmodifiableList(entries);
  }

  @Override
  public String type() {
    return gitObject.type();
  }

  @Override
  public String hash() {
    return gitObject.hash().toString();
  }

  @Override
  public String name() {
    return hash();
  }
}
