package git;

import java.io.File;

public record Git(File root, File objects, File refs, File head) {

  private static final File DEFAULT_ROOT = new File(".git");
  private static final File DEFAULT_OBJECTS = new File(DEFAULT_ROOT, "objects");
  private static final File DEFAULT_REFS = new File(DEFAULT_ROOT, "refs");
  private static final File DEFAULT_HEAD = new File(DEFAULT_ROOT, "HEAD");

  public Git() {
    this(DEFAULT_ROOT, DEFAULT_OBJECTS, DEFAULT_REFS, DEFAULT_HEAD);
  }

  public Git(File root) {
    this(root, new File(root, "objects"), new File(root, "refs"), new File(root, "HEAD"));
  }

}
