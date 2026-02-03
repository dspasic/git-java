package com.github.dspasic.gitjava;

import java.util.Arrays;

/**
 * Main entry point for the git-java application.
 * A simple Git implementation in Java with support for quiet mode.
 */
public class GitJava {
    private boolean quiet = false;
    
    public static void main(String[] args) {
        GitJava app = new GitJava();
        app.run(args);
    }
    
    /**
     * Run the git-java application with the given arguments.
     * 
     * @param args command line arguments
     */
    public void run(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        // Parse global flags
        int commandIndex = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--quiet") || args[i].equals("-q")) {
                quiet = true;
                commandIndex = i + 1;
            } else {
                commandIndex = i;
                break;
            }
        }
        
        if (commandIndex >= args.length) {
            printUsage();
            return;
        }
        
        String command = args[commandIndex];
        String[] commandArgs = Arrays.copyOfRange(args, commandIndex + 1, args.length);
        
        switch (command) {
            case "status":
                status(commandArgs);
                break;
            case "add":
                add(commandArgs);
                break;
            case "commit":
                commit(commandArgs);
                break;
            default:
                System.err.println("Unknown command: " + command);
                printUsage();
        }
    }
    
    /**
     * Set the quiet mode flag.
     * 
     * @param quiet true to enable quiet mode, false otherwise
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }
    
    /**
     * Check if quiet mode is enabled.
     * 
     * @return true if quiet mode is enabled, false otherwise
     */
    public boolean isQuiet() {
        return quiet;
    }
    
    private void printUsage() {
        if (!quiet) {
            System.out.println("Usage: git-java [--quiet|-q] <command> [<args>]");
            System.out.println();
            System.out.println("Commands:");
            System.out.println("  status    Show the working tree status");
            System.out.println("  add       Add file contents to the index");
            System.out.println("  commit    Record changes to the repository");
            System.out.println();
            System.out.println("Global options:");
            System.out.println("  --quiet, -q    Suppress informational messages");
        }
    }
    
    private void status(String[] args) {
        if (!quiet) {
            System.out.println("On branch main");
            System.out.println("Your branch is up to date with 'origin/main'.");
            System.out.println();
            System.out.println("nothing to commit, working tree clean");
        }
    }
    
    private void add(String[] args) {
        if (args.length == 0) {
            System.err.println("Nothing specified, nothing added.");
            return;
        }
        
        if (!quiet) {
            System.out.println("Adding files: " + String.join(", ", args));
        }
    }
    
    private void commit(String[] args) {
        String message = "Initial commit";
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-m") && i + 1 < args.length) {
                message = args[i + 1];
                break;
            }
        }
        
        if (!quiet) {
            System.out.println("[main " + generateCommitHash() + "] " + message);
            System.out.println(" 1 file changed, 1 insertion(+)");
        }
    }
    
    private String generateCommitHash() {
        // Generate a simple mock commit hash
        return String.format("%07x", System.currentTimeMillis() % 0x10000000);
    }
}
