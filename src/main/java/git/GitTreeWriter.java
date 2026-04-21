package git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    StringBuilder sb = new StringBuilder();

    for (GitTreeNode e : entries) {
      var entry = "%s %s\u0000%s".formatted(e.mode(), e.name(), e.hash());
      sb.append(entry);
    }

    int size = sb.toString().getBytes().length;

    sb.insert(0, "tree %d\u0000".formatted(size));

    byte[] content = sb.toString().getBytes();

    Hash hash = Hash.fromContent(content);

    Path dirname = git.objects().resolve(hash.dirname());
    Path filename = dirname.resolve(hash.filename());

    if (!Files.exists(dirname)) {
      Files.createDirectory(dirname);
    }

    Files.write(filename, ZlibCompressor.compress(sb.toString().getBytes()));

    return new GitTree(new GitObject(git, hash), entries);
  }
}
