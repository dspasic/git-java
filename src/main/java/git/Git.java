package git;

import java.io.IOException;
import java.nio.file.Path;

public record Git(Path root, Path objects, Path refs, Path head) {

  private static final Path DEFAULT_ROOT = Path.of(".git");
  private static final Path DEFAULT_OBJECTS = DEFAULT_ROOT.resolve("objects");
  private static final Path DEFAULT_REFS = DEFAULT_ROOT.resolve("refs");
  private static final Path DEFAULT_HEAD = DEFAULT_ROOT.resolve("HEAD");

  public Git() {
    this(DEFAULT_ROOT, DEFAULT_OBJECTS, DEFAULT_REFS, DEFAULT_HEAD);
  }

  public Git(Path root) throws IOException {
    this(
        root.toAbsolutePath(),
        root.toAbsolutePath().resolve("objects"),
        root.toAbsolutePath().resolve("refs"),
        root.toAbsolutePath().resolve("HEAD"));
  }
}
