package git.command;

import git.Git;
import git.GitObject;
import git.Hash;

public class CatFileCommand implements Command {

  private final Git git;

  public CatFileCommand(Git git) {
    this.git = git;
  }

  @Override
  public int execute(String[] args) {
    if (args.length < 3) {
      System.out.println("Usage: cat-file -p <hash>");
      return Command.EXIT_ERROR;
    }

    String subcmd = args[1];
    if (!subcmd.equals("-p")) {
      System.out.println("Unknown option: " + subcmd);
      return Command.EXIT_ERROR;
    }

    String hash = args[2];

    try {
      GitObject gitObject = new GitObject(git, new Hash(hash));
      System.out.print(gitObject.contentString());
    } catch (RuntimeException e) {
      System.err.println("Error reading object: " + hash);
      System.err.println("Error: " + e.getMessage());
      return Command.EXIT_ERROR;
    }
    return Command.EXIT_SUCCESS;
  }
}
