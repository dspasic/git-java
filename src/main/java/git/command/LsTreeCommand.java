package git.command;

import git.Git;
import git.GitObject;
import git.GitTree;
import java.io.IOException;

public class LsTreeCommand implements Command {

  private final Git git;

  public LsTreeCommand(Git git) {
    this.git = git;
  }

  @Override
  public int execute(String[] args) {
    if (args.length < 3) {
      System.out.println("Usage: ls-tree --name-only <hash>");
      return 1;
    }

    String subcmd = args[1];
    if (!subcmd.equals("--name-only")) {
      System.out.println("Unknown option: " + subcmd);
      return 1;
    }

    var hash = args[2];

    var gitObject = new GitObject(git, hash);

    try {
      var tree = new GitTree(gitObject);
      tree.entries().forEach(entry -> System.out.println(entry.name()));
      return 0;
    } catch (IOException | IllegalArgumentException e) {
      System.out.println("Error while reading file" + hash);
      System.out.println("Error: " + e.getMessage());
      return 1;
    }
  }
}
