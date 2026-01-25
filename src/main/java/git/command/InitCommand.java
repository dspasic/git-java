package git.command;

import git.Git;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

public class InitCommand implements Command {

  private final Git git;

  public InitCommand(Git git) {
    this.git = git;
  }

  @Override
  public int execute(String[] args) {
    try {
      if (Files.exists(git.objects())) {
        Files.createDirectory(git.objects());
      } else {
        System.err.println(
            "Reinitialized existing Git repository in " + git.root().toAbsolutePath());
        return 1;
      }
      if (Files.exists(git.refs())) {
        Files.createDirectory(git.refs());
      }

      Files.createFile(git.head());
      Files.write(git.head(), "ref: refs/heads/main\n".getBytes());
      System.out.println("Initialized git directory");
      return 0;
    } catch (IOException e) {
      System.err.println("Error while initializing repository: " + e.getMessage());
      return 0;
    }
  }
}
