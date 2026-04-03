package git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/// [Git Tree Objects](https://git-scm.com/book/en/v2/Git-Internals-Git-Objects#_tree_objects)
/// [What is the internal format of a Git tree
///
// object?](https://stackoverflow.com/questions/14790681/what-is-the-internal-format-of-a-git-tree-object)
public class GitTreeReader {

  private static final byte NUL = 0x00;
  private static final byte SPACE = 0x20;

  public static GitTree read(GitObject gitObject) throws IOException {
    byte[] content = gitObject.objectContent();
    List<GitTreeEntry> entries = new ArrayList<>();

    int pos = 0;
    int start = pos;
    long size;

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
        size = Long.parseLong(new String(content, start, pos - start));
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
      byte[] hashBytes = new byte[shaCount];
      start = pos;
      while (pos < content.length && shaCount > 0) {
        hashBytes[pos - start] = content[pos];
        pos++;
        shaCount--;
      }
      pos++;

      entries.add(new GitTreeEntry(mode, name, hashBytes));
    }

    return new GitTree(gitObject, entries);
  }
}
