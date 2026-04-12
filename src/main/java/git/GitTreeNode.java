package git;

import java.util.List;

public interface GitTreeNode {
  String type();
  List<? extends GitTreeNode> entries();
  String hash();
  default String name() {
    return hash();
  }
}
