
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestMain {
  public static void main(String[] args) throws IOException {
    Files.walkFileTree(Path.of("./src/main/"), new FileVisitorImpl());
  }

  public static class FileVisitorImpl implements FileVisitor<Path> {

    HashMap<Path, List<Path>> tree = new HashMap<>();

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
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
            v.add(file);
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
            v.add(dir);
            return v;
          });
      tree.remove(dir);
      return FileVisitResult.CONTINUE;
    }
  }
}
