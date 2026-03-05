package git.command;

import git.Git;
import git.GitObject;
import git.Hash;
import git.HashGenerator;
import git.ZlibCompressor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class HashObjectCommand implements Command {

  private final Git git;

  public HashObjectCommand(Git git) {
    this.git = git;
  }

  @Override
  public int execute(String[] args) {
    if (args.length < 3) {
      System.out.println("Usage: hash-object -w <file>");
      return 1;
    }

    String option = args[1];
    if (!option.equals("-w")) {
      System.out.println("Unknown subcommand: " + option);
      return 1;
    }

    String filePath = args[2];
    Path file = Path.of(filePath);
    if (!Files.exists(file)) {
      System.out.println("File not found: " + filePath);
      return 1;
    }

    try {
      var gitObject = GitObject.create(git, filePath);

      System.out.print(gitObject.hash().toString());
      return 0;
    } catch (IOException e) {
      System.err.println("Error reading file: " + filePath);
      System.err.println("Error: " + e.getMessage());
      return 1;
    } catch (NoSuchAlgorithmException e) {
      System.err.println("Error generating hash: SHA-1");
      System.err.println("Error: " + e.getMessage());
      return 1;
    }
  }
}
