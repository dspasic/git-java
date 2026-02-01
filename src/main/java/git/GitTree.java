package git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitTree {

  private Integer size;
  private final GitObject gitObject;
  private final List<GitTreeEntry> entries = new ArrayList<>();

  private static final byte NUL = 0x20;
  private static final byte SPACE = 0x20;

  public GitTree(GitObject gitObject) throws IOException {
    this.gitObject = gitObject;
    readTree();
  }

  /// [Git Tree Objects](https://git-scm.com/book/en/v2/Git-Internals-Git-Objects#_tree_objects)
  /// [What is the internal format of a Git tree
  /// object?](https://stackoverflow.com/questions/14790681/what-is-the-internal-format-of-a-git-tree-object)
  private void readTree() throws IOException {
    byte[] content = gitObject.readObjectContent();

    int pos = 0;
    int start = pos;

    // read header
    while (pos < content.length) {
      if (content[pos] == SPACE) {
        String type = new String(content, start, pos - start);
        if (!type.equals("tree")) {
          throw new RuntimeException(
              String.format("Given object must be from type tree. Found type: %s", type));
        }
        pos++;
        start = pos;
      }

      if (content[pos] == NUL) {
        size = Integer.parseInt(new String(content, start, pos - start));
        pos++;
        break;
      }

      pos++;
    }

    // read entries
    while (pos < content.length) {
      start = pos;

      while (content[pos] != SPACE) {
        pos++;
      }
      String mode = new String(content, start, pos - start);
      pos++;

      start = pos;
      while (content[pos] != NUL) {
        pos++;
      }
      String name = new String(content, start, pos - start);
      pos++;

      int shaCount = 20;
      start = pos;
      while (pos < content.length && shaCount > 0) {
        pos++;
        shaCount--;
      }
      String sha = new String(content, start, shaCount);
      pos++;

      entries.add(new GitTreeEntry(mode, name, sha));
    }
  }

  public Integer size() {
    return size;
  }

  public List<GitTreeEntry> entries() {
    return Collections.unmodifiableList(entries);
  }
}
