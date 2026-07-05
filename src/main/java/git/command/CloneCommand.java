package git.command;

import git.Git;
import git.GitClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

public class CloneCommand implements Command {

  private final Git git;

  public CloneCommand(Git git) {
    this.git = Objects.requireNonNull(git, "git must not be null");
  }

  @Override
  public int execute(String[] args) {
    System.out.println("Hello, Clone");

    if (args.length != 2) {
      System.out.printf("Command must have 2 args. %d found. Given: %s%n", args.length, Arrays.toString(args));
    }

    try {
      GitClient client = new GitClient(args[1]);
      System.out.println(client.gitUploadPack());
    } catch (IOException | InterruptedException | URISyntaxException ex) {
      System.err.printf("Error occurred while fetching git upload pack. Error: %s%n", ex.getMessage());
      return EXIT_ERROR;
    }

    return EXIT_SUCCESS;
  }
}
