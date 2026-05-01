package git;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GitTreeWriter {

  public static GitTree write(Git git, List<? extends GitTreeNode> entries)
      throws NoSuchAlgorithmException, IOException {
    Objects.requireNonNull(git, "git must not be null.");
    Objects.requireNonNull(entries, "entries must not be null.");

    if (entries.isEmpty()) {
      return null;
    }

    entries.sort(Comparator.comparing(GitTreeNode::name));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (GitTreeNode e : entries) {
      String entryHeader = "%s %s\u0000".formatted(e.mode(), e.name());
      baos.write(entryHeader.getBytes());
      baos.write(e.hash().bytes());
    }

    byte[] treeHeader = "tree %d\u0000".formatted(baos.size()).getBytes();
    byte[] content = new byte[treeHeader.length + baos.size()];

    System.arraycopy(treeHeader, 0, content, 0, treeHeader.length);
    System.arraycopy(baos.toByteArray(), 0, content, treeHeader.length, baos.size());

    GitObject object = git.writeObject(content);

    return new GitTree(object, entries);
  }
}
