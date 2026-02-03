package git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class GitObject {

  private final Hash hash;
  public final Git git;
  private final Path path;
  private String type;
  private byte[] content;
  private Integer size;
  private int pos;
  private int start;

  public GitObject(Git git, Hash hash) {
    this.hash = hash;
    this.git = git;
    path = git.objects().resolve(hash.dirname(), hash.filename());
  }

  private void parseObject() {
    if (content != null) {
      return;
    }
    if (!exists()) {
      throw new IllegalArgumentException("Object not found: " + hash);
    }
    if (!Files.isReadable(path)) {
      throw new IllegalArgumentException("Cannot read object: " + hash);
    }
    if (!Files.isRegularFile(path)) {
      throw new IllegalArgumentException("Not a regular file: " + hash);
    }

    try {
      content = ZlibCompressor.decompress(Files.readAllBytes(path));
    } catch (IOException e) {
      throw new RuntimeException("Error decompressing object: " + path, e);
    }

    pos = 0;
    start = pos;
    while (pos < content.length) {
      if (content[pos] == 0x20) {
        type = new String(content, start, pos - start);
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

    if (type == null) {
      throw new RuntimeException("type not found in object");
    }

    if (size == null) {
      throw new RuntimeException("size not found in object");
    }

    if ((pos + size) != content.length) {
      throw new RuntimeException(
          String.format(
              "size \"%d\" of object does not match content length \"%d\"",
              (pos + size), content.length));
    }
  }

  int contentStart() {
    parseObject();
    return start;
  }

  int contentPos() {
    parseObject();
    return pos;
  }

  public byte[] objectContent() throws IOException {
    parseObject();
    return content;
  }

  public String contentString() {
    parseObject();
    return new String(content, start, (pos + size) - start);
  }

  public byte[] content() {
    return Arrays.copyOfRange(content, start, (pos + size) + 1);
  }

  public String type() {
    parseObject();
    return type;
  }

  public long size() {
    parseObject();
    return size;
  }

  public Hash hash() {
    return hash;
  }

  public Path path() {
    return path;
  }

  public boolean exists() {
    return Files.exists(path);
  }

  @Override
  public String toString() {
    return hash.toString();
  }
}
