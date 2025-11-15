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
  public void execute(String[] args) {
    try {
      if (Files.exists(git.objects())) {
        Files.createDirectory(git.objects());
      } else {
        System.err.println(
            "Reinitialized existing Git repository in " + git.root().toAbsolutePath());
        System.exit(1);
      }
      if (Files.exists(git.refs())) {
        Files.createDirectory(git.refs());
      }

      Files.createFile(git.head());
      Files.write(git.head(), "ref: refs/heads/main\n".getBytes());
      System.out.println("Initialized git directory");
      System.exit(0);
    } catch (IOException e) {
      System.err.println("Error initializing repository: " + e.getMessage());
      System.exit(1);
    }
  }
}
