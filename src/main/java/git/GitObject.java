package git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GitObject {

  private final String hash;
  public final Git git;
  private final File file;

  public GitObject(Git git, String hash) {
    if (hash.length() != 40 || !hash.matches("[a-fA-F0-9]+")) {
      throw new IllegalArgumentException("Invalid hash: " + hash);
    }
    this.hash = hash;
    this.git = git;
    String directory = hash.substring(0, 2);
    String filename = hash.substring(2);
    file = new File(new File(git.objects(), directory), filename);
  }

  public String hash() {
    return hash;
  }

  public File file() {
    return file;
  }

  public boolean exists() {
    return file().exists();
  }

  public byte[] readObjectContent() throws IOException {
    if (!exists()) {
      throw new IllegalArgumentException("Object not found: " + hash);
    }
    if (!file.canRead()) {
      throw new IllegalArgumentException("Cannot read object: " + hash);
    }
    return ZlibCompressor.decompress(Files.readAllBytes(file().toPath()));
  }

  @Override
  public String toString() {
    return hash;
  }
}
