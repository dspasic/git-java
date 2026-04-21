package git;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class GitObject {

  private final Hash hash;
  public final Git git;
  private final Path path;
  private String type;
  private byte[] content;
  private Long size;
  private int pos;
  private int start;

  public GitObject(Git git, Hash hash) {
    this.hash = hash;
    this.git = git;
    path = git.objects().resolve(hash.dirname(), hash.filename());
  }

  public static GitObject create(Git git, Path path) throws IOException, NoSuchAlgorithmException {
    if (!Files.exists(path)) {
      throw new FileNotFoundException("File not found: " + path);
    }

    var fileSize = Files.size(path);
    var objectContent =
        String.format("blob %d\u0000%s", fileSize, Files.readString(path)).getBytes();
    Hash hash = Hash.fromContent(objectContent);
    String dirname = hash.dirname();
    String filename = hash.filename();

    var objectDirPath = git.objects().resolve(dirname);
    if (!Files.exists(objectDirPath)) {
      Files.createDirectory(objectDirPath);
    }
    var objectPath = objectDirPath.resolve(filename);
    var compressedContent = ZlibCompressor.compress(objectContent);
    Files.write(objectPath, compressedContent);

    var gitObject = new GitObject(git, hash);
    gitObject.content = objectContent;
    gitObject.type = "blob";
    gitObject.size = fileSize;
    gitObject.pos = gitObject.type.length() + gitObject.size.toString().length() + 3;
    gitObject.start = gitObject.pos;

    return gitObject;
  }

  public static GitObject create(Git git, String path)
      throws IOException, NoSuchAlgorithmException {
    return create(git, Path.of(path));
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
        size = Long.parseLong(new String(content, start, pos - start));
        pos++;
        start = pos;
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

  public byte[] objectContent() {
    parseObject();
    return content;
  }

  public String contentString() {
    parseObject();
    return new String(content, start, (pos + size.intValue()) - start);
  }

  public byte[] content() {
    parseObject();
    return Arrays.copyOfRange(content, start, (pos + size.intValue()) + 1);
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
