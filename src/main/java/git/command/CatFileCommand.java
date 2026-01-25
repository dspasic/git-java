package git.command;

import git.Git;
import git.ZlibCompressor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CatFileCommand implements Command {

  private final Git git;

  public CatFileCommand(Git git) {
    this.git = git;
  }

  @Override
  public int execute(String[] args) {
    if (args.length < 3) {
      System.out.println("Usage: cat-file -p <hash>");
      return 1;
    }

    String subcmd = args[1];
    if (!subcmd.equals("-p")) {
      System.out.println("Unknown option: " + subcmd);
      return 1;
    }

    String hash = args[2];
    if (hash.length() != 40 || !hash.matches("[a-fA-F0-9]+")) {
      System.out.println("Invalid hash: " + hash);
      return 1;
    }

    String dirname = hash.substring(0, 2);
    String filename = hash.substring(2);

    Path objectPath = git.objects().resolve(dirname, filename);
    if (!Files.exists(objectPath)) {
      System.out.println("Object not found: " + hash);
      return 1;
    }

    try {
      byte[] compressedContent = Files.readAllBytes(objectPath);
      String objectContent = new String(ZlibCompressor.decompress(compressedContent));
      System.out.print(objectContent.substring(objectContent.indexOf(0x00) + 1));
    } catch (IOException e) {
      System.err.println("Error reading object: " + hash);
      System.err.println("Error: " + e.getMessage());
      return 1;
    }
    return 0;
  }
}
