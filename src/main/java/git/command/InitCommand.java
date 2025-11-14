package git.command;

import git.Git;
import java.io.IOException;
import java.nio.file.Files;

public class InitCommand implements Command {

  private final Git git;

  public InitCommand(Git git) {
    this.git = git;
  }

  @Override
  public void execute(String[] args) {
    if (!git.objects().exists()) {
      git.objects().mkdirs();
    } else {
      System.err.println("Reinitialized existing Git repository in " + Git.DEFAULT_ROOT.getAbsolutePath());
      System.exit(1);
    }

    if (!git.refs().exists()) {
      git.refs().mkdirs();
    }

    try {
      git.head().createNewFile();
      Files.write(git.head().toPath(), "ref: refs/heads/main\n".getBytes());
      System.out.println("Initialized git directory");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
