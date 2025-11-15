package git;

import java.nio.file.Path;

public record Git(Path root, Path objects, Path refs, Path head) {

  private static final Path DEFAULT_ROOT = Path.of(".git");
  private static final Path DEFAULT_OBJECTS = DEFAULT_ROOT.resolve("objects");
  private static final Path DEFAULT_REFS = DEFAULT_ROOT.resolve( "refs");
  private static final Path DEFAULT_HEAD = DEFAULT_ROOT.resolve( "HEAD");

  public Git() {
    this(DEFAULT_ROOT, DEFAULT_OBJECTS, DEFAULT_REFS, DEFAULT_HEAD);
  }

  public Git(Path root) {
    this(root, root.resolve("objects"), root.resolve( "refs"), root.resolve("HEAD"));
  }

}
