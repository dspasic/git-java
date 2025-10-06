import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Main {

  static final File root = new File(".git");
  static final File objects = new File(root, "objects");
  static final File refs = new File(root, "refs");
  static final File head = new File(refs, "HEAD");

  public static void main(String[] args) {
    final String command = args[0];

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
      default -> System.out.println("Unknown command: " + command);
    }
  }

  interface Command {
    void execute(String[] args);
  }

  static class InitCommand implements Command {
    @Override
    public void execute(String[] args) {
      if (!objects.exists()) {
        objects.mkdirs();
      } else {
        System.out.println("Reinitialized existing Git repository in " + root.getAbsolutePath());
        return;
      }
      if (!refs.exists()) {
        refs.mkdirs();
      }

      try {
        head.createNewFile();
        Files.write(head.toPath(), "ref: refs/heads/main\n".getBytes());
        System.out.println("Initialized git directory");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static class HashObjectCommand implements Command {
    @Override
    public void execute(String[] args) {
      System.out.println(Arrays.toString(args));
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
        var hash = HashGenerator.generateHash(file);
        var dirname = hash.substring(0, 2);
        var filename = hash.substring(2);

        var objectDir = new File(objects, dirname);
        if (!objectDir.exists()) {
          objectDir.mkdirs();
        }

        var objectFile = new File(objectDir, filename);
        var objectContent =
            String.format("blob %d\0%s", file.length(), Files.readString(file.toPath()));
        var compressedContent = ZlibCompressor.compress(objectContent.getBytes());
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
      System.out.println(Arrays.toString(args));
      if (args.length < 3) {
        System.out.println("Usage: cat-file -p <hash>");
        System.exit(1);
      }

      String subcmd = args[1];
      if (!subcmd.equals("-p")) {
        System.out.println("Unknown subcommand: " + subcmd);
        System.exit(1);
      }

      String hash = args[2];
      if (hash.length() != 40 || !hash.matches("[a-fA-F0-9]+")) {
        System.out.println("Invalid hash: " + hash);
        System.exit(1);
      }

      String dirname = hash.substring(0, 2);
      String filename = hash.substring(2);

      File objectFile = new File(new File(objects, dirname), filename);
      if (!objectFile.exists()) {
        System.out.println("Object not found: " + hash);
        System.exit(1);
      }

      try {
        byte[] compressedContent = Files.readAllBytes(objectFile.toPath());
        byte[] objectContent = ZlibCompressor.decompress(compressedContent);
        System.out.print(new String(objectContent));
      } catch (IOException e) {
        System.err.println("Error reading object: " + hash);
        System.err.println("Error: " + e.getMessage());
        System.exit(1);
      }
    }
  }

  static class HashGenerator {
    public static String generateHash(File file) throws NoSuchAlgorithmException, IOException {
      var digest = MessageDigest.getInstance("SHA-1");
      byte[] fileBytes = Files.readAllBytes(file.toPath());
      byte[] hashBytes = digest.digest(fileBytes);
      StringBuilder sb = new StringBuilder();
      for (byte b : hashBytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    }
  }

  static class ZlibCompressor {
    public static byte[] compress(byte[] data) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try (DeflaterOutputStream deflaterOutputStream =
          new DeflaterOutputStream(byteArrayOutputStream)) {
        deflaterOutputStream.write(data);
      }
      return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decompress(byte[] data) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try (InflaterInputStream inflaterInputStream =
          new InflaterInputStream(new ByteArrayInputStream(data))) {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inflaterInputStream.read(buffer)) != -1) {
          byteArrayOutputStream.write(buffer, 0, len);
        }
      }
      return byteArrayOutputStream.toByteArray();
    }
  }
}
