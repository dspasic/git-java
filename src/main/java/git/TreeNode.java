package git;

import java.util.List;

public interface TreeNode {
  String type();
  List<TreeNode> entries();
  String hash();
  default String name() {
    return hash();
  }
}
