package git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GitObject {

  private final String hash;
  public final Git git;
  private final Path path;

  public GitObject(Git git, String hash) {
    if (hash.length() != 40 || !hash.matches("[a-fA-F0-9]+")) {
      throw new IllegalArgumentException("Invalid hash: " + hash);
    }
    this.hash = hash;
    this.git = git;
    String directory = hash.substring(0, 2);
    String filename = hash.substring(2);
    path = git.objects().resolve(directory, filename);
  }

  public String hash() {
    return hash;
  }

  public Path path() {
    return path;
  }

  public boolean exists() {
    return Files.exists(path);
  }

  public byte[] readObjectContent() throws IOException {
    if (!exists()) {
      throw new IllegalArgumentException("Object not found: " + hash);
    }
    if (!Files.isReadable(path)) {
      throw new IllegalArgumentException("Cannot read object: " + hash);
    }
    return ZlibCompressor.decompress(Files.readAllBytes(path));
  }

  @Override
  public String toString() {
    return hash;
  }
}
