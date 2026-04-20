package git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    Objects.requireNonNull(git, "git must not be null.");
    Objects.requireNonNull(entries, "entries must not be null.");

    if (entries.isEmpty()) {
      return null;
    }

    entries.sort(Comparator.comparing(GitTreeNode::name));

    StringBuilder sb = new StringBuilder();

    for (GitTreeNode e : entries) {
      System.out.println(e);
      sb.append("%s %s\u0000%s".formatted(e.mode(), e.name(), e.hash()));
    }

    sb.insert(0, "tree %d\u0000".formatted(sb.length()));

    Hash hash = Hash.fromContent(sb.toString().getBytes());

    Path dirname = git.objects().resolve(hash.dirname());
    Path filename = dirname.resolve(hash.filename());

    if (!Files.exists(dirname)) {
      Files.createDirectory(dirname);
    }

    if (!Files.exists(filename)) {
      Files.createFile(filename);
    }

    Files.write(filename, hash.bytes());

    return new GitTree(new GitObject(git, hash), entries);
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
  public String hash() {
    return gitObject.hash().toString();
  }

  @Override
  public String name() {
    return hash();
  }
}
