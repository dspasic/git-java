package git.command;

interface Command {
  int EXIT_ERROR = 1;
  int EXIT_SUCCESS = 0;

  int execute(String[] args);
}
