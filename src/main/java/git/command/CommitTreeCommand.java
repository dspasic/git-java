package git.command;

import git.Git;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class CommitTreeCommand implements Command {

  private final Git git;

  private static final String AUTHOR = "Max Mustermann <max.mustermann@abc.com>";
  private static final String COMMITTER = CommitTreeCommand.AUTHOR;
  private static final String DATETIME = "1234567890 +0000";

  public CommitTreeCommand(Git git) {
    this.git = Objects.requireNonNull(git, "git must not be null.");
  }

  @Override
  public int execute(String[] args) {
    if (args.length != 6) {
      System.out.println("Usage commit-tree <tree_sha> -p <commit_sha> -m <message>");
      return Command.EXIT_ERROR;
    }

    String treeSha = args[1];
    String commitSha = args[3];
    String message = args[5];

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      baos.write("tree %s%n".formatted(treeSha).getBytes());
      baos.write("parent %s%n".formatted(commitSha).getBytes());
      baos.write("author %s %s%n".formatted(AUTHOR, DATETIME).getBytes());
      baos.write("committer %s %s%n%n".formatted(COMMITTER, DATETIME).getBytes());
      baos.write(message.getBytes());

      byte[] commitHeader = "commit %n\u0000".formatted(baos.size()).getBytes();
      byte[] content = new byte[commitHeader.length + baos.size()];

      System.arraycopy(commitHeader, 0, content, 0, commitHeader.length);
      System.arraycopy(baos.toByteArray(), 0, content, commitHeader.length, baos.size());

      git.writeObject(content);
    } catch (IOException ex) {
      System.err.println("Erorr while writting commit. Error: %s".formatted(ex.getMessage()));
      return Command.EXIT_ERROR;
    } catch (NoSuchAlgorithmException ex) {
      System.err.println("Erorr while writting commit. Error: %s".formatted(ex.getMessage()));
      return Command.EXIT_ERROR;
    }

    return Command.EXIT_SUCCESS;
  }
}
