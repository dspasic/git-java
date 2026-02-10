import git.Git;
import git.command.CatFileCommand;
import git.command.HashObjectCommand;
import git.command.InitCommand;
import git.command.LsTreeCommand;
import git.command.WriteTreeCommand;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

  public static void main(String[] args) throws IOException {
    final String command = args[0];
    int exitCode = 0;

    switch (command) {
      case "init" -> {
        var cmd = new InitCommand(new Git());
        exitCode = cmd.execute(args);
      }
      case "hash-object" -> {
        var git = new Git(Path.of(".git"));
        var cmd = new HashObjectCommand(git);
        exitCode = cmd.execute(args);
      }
      case "cat-file" -> {
        var git = new Git(Path.of(".git"));
        var cmd = new CatFileCommand(git);
        exitCode = cmd.execute(args);
      }
      case "ls-tree" -> {
        var git = new Git(Path.of(".git"));
        var cmd = new LsTreeCommand(git);
        exitCode = cmd.execute(args);
      }
      case "write-tree" -> {
        var git = new Git(Path.of(".git"));
        var cmd = new WriteTreeCommand(git);
        exitCode = cmd.execute(args);
      }
      case "help" -> {
        System.out.println("Usage: git <command> [<args>]");
        System.out.println("Available commands:");
        System.out.println("  init");
        System.out.println("  hash-object -w <file>");
        System.out.println("  cat-file -p <hash>");
        System.out.println("  ls-tree --name-only <hash>");
        System.out.println("  write-tree");
      }
      default -> {
        System.err.println("Unknown command: " + command);
        exitCode = 1;
      }
    }

    System.exit(exitCode);
  }
}
