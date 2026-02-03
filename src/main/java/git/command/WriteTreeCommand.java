package git.command;

import git.Git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class WriteTreeCommand implements Command {

  private final Git git;

  public WriteTreeCommand(Git git) {
    this.git = Objects.requireNonNull(git, "Git must be not null");
  }

  @Override
  public int execute(String[] args) {
    try (Stream<Path> paths = Files.walk(Paths.get(".").toRealPath())) {
      paths
          .filter(
              p -> !p.startsWith(git.root()))
          .forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }
}
