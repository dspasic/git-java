package git.command;

import java.nio.file.Paths;

public class WriteTreeCommand implements Command {

  @Override
  public void execute(String[] args) {
    var p = Paths.get(".");
  }
}
