import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Main {

  static final File ROOT = new File(".git");
  static final File OBJECTS = new File(ROOT, "objects");
  static final File REFS = new File(ROOT, "refs");
  static final File HEAD = new File(ROOT, "HEAD");

  public static void main(String[] args) {
    final String command = args[0];
    int exitCode = 0;

    switch (command) {
      case "init" -> {
        var cmd = new InitCommand();
        cmd.execute(args);
      }
      case "hash-object" -> {
        var cmd = new HashObjectCommand();
        cmd.execute(args);
      }
      case "cat-file" -> {
        var cmd = new CatFileCommand();
        cmd.execute(args);
      }
      case "ls-tree" -> {
        var cmd = new LsTreeCommand();
        cmd.execute(args);
      }
      case "help" -> {
        System.out.println("Usage: git <command> [<args>]");
        System.out.println("Available commands:");
        System.out.println("  init");
        System.out.println("  hash-object -w <file>");
        System.out.println("  cat-file -p <hash>");
        System.out.println("  ls-tree --name-only <hash>");
      }
      default -> {
        System.err.println("Unknown command: " + command);
        exitCode = 1;
      }
    }

    System.exit(exitCode);
  }

  interface Command {
    void execute(String[] args);
  }

  static class InitCommand implements Command {

    @Override
    public void execute(String[] args) {
      if (!OBJECTS.exists()) {
        OBJECTS.mkdirs();
      } else {
        System.err.println("Reinitialized existing Git repository in " + ROOT.getAbsolutePath());
        System.exit(1);
      }

      if (!REFS.exists()) {
        REFS.mkdirs();
      }

      try {
        HEAD.createNewFile();
        Files.write(HEAD.toPath(), "ref: refs/heads/main\n".getBytes());
        System.out.println("Initialized git directory");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static class HashObjectCommand implements Command {

    @Override
    public void execute(String[] args) {
      if (args.length < 3) {
        System.out.println("Usage: hash-object -w <file>");
        System.exit(1);
      }

      String option = args[1];
      if (!option.equals("-w")) {
        System.out.println("Unknown subcommand: " + option);
        System.exit(1);
      }

      String filePath = args[2];
      File file = new File(filePath);
      if (!file.exists()) {
        System.out.println("File not found: " + filePath);
        System.exit(1);
      }

      try {
        var objectContent =
            String.format("blob %d\u0000%s", file.length(), Files.readString(file.toPath()))
                .getBytes();

        var hash = HashGenerator.generateHash(objectContent);
        var dirname = hash.substring(0, 2);
        var filename = hash.substring(2);

        var objectDir = new File(OBJECTS, dirname);
        if (!objectDir.exists()) {
          objectDir.mkdirs();
        }
        var objectFile = new File(objectDir, filename);

        var compressedContent = ZlibCompressor.compress(objectContent);
        Files.write(objectFile.toPath(), compressedContent);

        System.out.print(hash);
      } catch (IOException e) {
        System.err.println("Error reading file: " + filePath);
        System.err.println("Error: " + e.getMessage());
        System.exit(1);
      } catch (NoSuchAlgorithmException e) {
        System.err.println("Error generating hash: SHA-1");
        System.err.println("Error: " + e.getMessage());
        System.exit(1);
      }
    }
  }

  static class CatFileCommand implements Command {

    @Override
    public void execute(String[] args) {
      if (args.length < 3) {
        System.out.println("Usage: cat-file -p <hash>");
        System.exit(1);
      }

      String subcmd = args[1];
      if (!subcmd.equals("-p")) {
        System.out.println("Unknown option: " + subcmd);
        System.exit(1);
      }

      String hash = args[2];
      if (hash.length() != 40 || !hash.matches("[a-fA-F0-9]+")) {
        System.out.println("Invalid hash: " + hash);
        System.exit(1);
      }

      String dirname = hash.substring(0, 2);
      String filename = hash.substring(2);

      File objectFile = new File(new File(OBJECTS, dirname), filename);
      if (!objectFile.exists()) {
        System.out.println("Object not found: " + hash);
        System.exit(1);
      }

      try {
        byte[] compressedContent = Files.readAllBytes(objectFile.toPath());
        String objectContent = new String(ZlibCompressor.decompress(compressedContent));
        System.out.print(objectContent.substring(objectContent.indexOf(0x00) + 1));
      } catch (IOException e) {
        System.err.println("Error reading object: " + hash);
        System.err.println("Error: " + e.getMessage());
        System.exit(1);
      }
    }
  }

  static class LsTreeCommand implements Command {

    @Override
    public void execute(String[] args) {
      if (args.length < 3) {
        System.out.println("Usage: ls-tree --name-only <hash>");
        System.exit(1);
      }

      String subcmd = args[1];
      if (!subcmd.equals("--name-only")) { System.out.println("Unknown option: " + subcmd);
        System.exit(1);
      }

      var hash = args[2];

      var gitObject = new GitObject(hash);

      try {
        var content = new String(gitObject.readObjectContent());
        var type = content.substring(0, content.indexOf(" "));
        var size =
            Integer.parseInt(content.substring(content.indexOf(" ") + 1, content.indexOf(0x00)));

        var pos = content.indexOf(0x00) + 1;

        while (pos < size) {
          var numberPosition = content.indexOf(" ", pos);
          var number = content.substring(pos, numberPosition);

          var namePosition = content.indexOf(0x00, numberPosition + 1);
          var name = content.substring(numberPosition + 1, namePosition);

          var endIndex = determineEndIndex(namePosition, content);
          var sha = content.substring(namePosition + 1, Math.min(endIndex, content.length()));

          System.out.printf("%s\n", name);
          pos = endIndex;
        }
      } catch (IOException | IllegalArgumentException e) {
        System.out.println("Error while reading file" + hash);
        System.out.println("Error: " + e.getMessage());
        System.exit(1);
      }
    }

    private static int determineEndIndex(int namePosition, String content) {
      return 20;
//      var byteLimit = 20;
//      var utf8Bytes = 0;
//      var endIndex = namePosition + 1;
//      for (var i = namePosition + 1; i < content.length(); i++) {
//        char c = content.charAt(i);
//        if (Character.isHighSurrogate(c)) {
//          continue;
//        }
//        if (Character.isLowSurrogate(c)) {
//          // Low surrogate contributes 4 bytes in UTF-8
//          utf8Bytes += 4;
//        } else if (c < 0x007F) {
//          // ASCII characters: 1 byte
//          utf8Bytes += 1;
//        } else if (c < 0x07FF) {
//          // Two-byte UTF-8 characters
//          utf8Bytes += 2;
//        } else {
//          // Three-byte UTF-8 characters
//          utf8Bytes += 3;
//        }
//        if (utf8Bytes > byteLimit) {
//          break;
//        }
//        endIndex = i + 1;
//      }
//      return endIndex;
    }
  }

  static class GitObject {

    private final String hash;
    private final String directory;
    private final String filename;

    GitObject(String hash) {
      if (hash.length() != 40 || !hash.matches("[a-fA-F0-9]+")) {
        throw new IllegalArgumentException("Invalid hash: " + hash);
      }
      this.hash = hash;
      this.directory = hash.substring(0, 2);
      this.filename = hash.substring(2);
    }

    String hash() {
      return hash;
    }

    File file() {
      return new File(new File(OBJECTS, directory), filename);
    }

    boolean exists() {
      return file().exists();
    }

    byte[] readObjectContent() throws IOException {
      var f = file();
      if (!f.exists()) {
        throw new IllegalArgumentException("Object not found: " + hash);
      }
      if (!f.canRead()) {
        throw new IllegalArgumentException("Cannot read object: " + hash);
      }
      return ZlibCompressor.decompress(Files.readAllBytes(file().toPath()));
    }

    @Override
    public String toString() {
      return hash;
    }
  }

  static class HashGenerator {

    public static String generateHash(byte[] data) throws NoSuchAlgorithmException, IOException {
      var digest = MessageDigest.getInstance("SHA-1");
      byte[] hashBytes = digest.digest(data);
      StringBuilder sb = new StringBuilder();
      for (byte b : hashBytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    }
  }

  static class ZlibCompressor {

    static final int BUFFER_SIZE = 8192;

    public static byte[] compress(byte[] data) throws IOException {
      var baos = new ByteArrayOutputStream();
      try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(baos)) {
        deflaterOutputStream.write(data);
      }
      return baos.toByteArray();
    }

    public static byte[] decompress(byte[] data) throws IOException {
      var baos = new ByteArrayOutputStream();
      try (InflaterInputStream inflaterInputStream =
          new InflaterInputStream(new ByteArrayInputStream(data))) {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = inflaterInputStream.read(buffer)) != -1) {
          baos.write(buffer, 0, len);
        }
      }
      return baos.toByteArray();
    }
  }
}
