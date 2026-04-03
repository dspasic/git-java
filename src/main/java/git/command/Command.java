package git.command;

interface Command {
  public static int EXIT_ERROR = 1;
  public static int EXIT_SUCCESS = 0;

  int execute(String[] args);
}
