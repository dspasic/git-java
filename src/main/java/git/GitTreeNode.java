package git;

import java.util.List;

public interface GitTreeNode {

  String mode();

  List<? extends GitTreeNode> entries();

  Hash hash();

  String name();
}
