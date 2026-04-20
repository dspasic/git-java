package git.command;

import git.Git;
import git.GitObject;
import git.GitTree;
import git.GitTreeEntry;
import git.GitTreeNode;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WriteTreeCommand implements Command {

  private final Git git;

  public WriteTreeCommand(Git git) {
    this.git = Objects.requireNonNull(git, "Git must be not null.");
  }

  @Override
  public int execute(String[] args) {
    try {
      FileVisitor<Path> fv = new FileVisitorImpl(git);
      Files.walkFileTree(Path.of("."), fv);
    } catch (IOException ex) {
      System.err.println("Could not read the directory. Error: " + ex.getMessage());
      return Command.EXIT_ERROR;
    }
    return Command.EXIT_SUCCESS;
  }

  static class FileVisitorImpl implements FileVisitor<Path> {

    private final Git git;
    private final HashMap<Path, List<GitTreeNode>> tree = new HashMap<>();

    FileVisitorImpl(Git git) {
      this.git = Objects.requireNonNull(git, "git must be not null.");
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
      if (dir.toAbsolutePath().normalize().equals(git.root())) {
        System.out.println("Skipping Dir:" + dir.toAbsolutePath().normalize());
        return FileVisitResult.SKIP_SIBLINGS;
      }
      System.out.println("Pre visit dir:" + dir);
      tree.put(dir, new ArrayList<>());
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
      System.out.println("Visit file:" + file);
      tree.computeIfPresent(
          file.getParent(),
          (_, v) -> {
            try {
              GitObject o = GitObject.create(git, file);
              v.add(new GitTreeEntry("blob", file.toString(), o.hash().bytes()));
            } catch (NoSuchAlgorithmException | IOException ex) {
              System.err.println(
                  "Cannot process file: " + file + ". Error message: " + ex.getMessage());
            }
            return v;
          });
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
      System.out.println("Visit failed:" + file + ". Here is why: " + exc.getMessage());
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
      System.out.println("Post dir: " + dir);
      System.out.println("Creating tree with its entries: " + dir);
      tree.getOrDefault(dir, new ArrayList<>()).forEach(System.out::println);
      tree.computeIfPresent(
          dir.getParent(),
          (k, v) -> {
            System.out.printf("Add tree %s to its parent %s%n", dir, k);

//            GitTree.create()
//


//            try {
//              v.add(new GitTreeEntry("tree", dir.toString(), o.hash().bytes()));
//            } catch (NoSuchAlgorithmException | IOException ex) {
//              System.err.println(
//                  "Cannot process file: " + dir + ". Error message: " + ex.getMessage());
//            }
            return v;
          });
      tree.remove(dir);
      return FileVisitResult.CONTINUE;
    }
  }
}
