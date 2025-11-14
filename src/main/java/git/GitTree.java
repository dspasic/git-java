package git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitTree {

  private Integer size;
  private final GitObject gitObject;
  private final List<GitTreeEntry> entries = new ArrayList<>();

  public GitTree(GitObject gitObject) throws IOException {
    this.gitObject = gitObject;
    readTree();
  }

  private void readTree() throws IOException {
    var content = gitObject.readObjectContent();

    int pos = 0;
    int start = pos;

    // read header
    while (pos < content.length) {
      if (content[pos] == 0x20) {
        String type = new String(content, start, pos - start);
        pos++;
        start = pos;
      }

      if (content[pos] == 0x00) {
        size = Integer.parseInt(new String(content, start, pos - start));
        pos++;
        break;
      }

      pos++;
    }

    // read entries
    while (pos < content.length) {
      int shaCount = 20;
      start = pos;

      while (content[pos] != 0x20) {
        pos++;
      }
      String mode = new String(content, start, pos - start);
      pos++;

      start = pos;
      while (content[pos] != 0x00) {
        pos++;
      }
      String name = new String(content, start, pos - start);
      pos++;

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
