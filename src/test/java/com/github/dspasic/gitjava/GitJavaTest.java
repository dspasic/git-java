package com.github.dspasic.gitjava;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GitJava application, focusing on quiet mode functionality.
 */
class GitJavaTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @Test
    void testQuietModeWithLongFlag() {
        GitJava app = new GitJava();
        app.run(new String[]{"--quiet", "status"});
        
        assertTrue(app.isQuiet(), "Quiet mode should be enabled with --quiet flag");
        assertEquals("", outContent.toString(), "No output should be produced in quiet mode");
    }
    
    @Test
    void testQuietModeWithShortFlag() {
        GitJava app = new GitJava();
        app.run(new String[]{"-q", "status"});
        
        assertTrue(app.isQuiet(), "Quiet mode should be enabled with -q flag");
        assertEquals("", outContent.toString(), "No output should be produced in quiet mode");
    }
    
    @Test
    void testVerboseModeStatus() {
        GitJava app = new GitJava();
        app.run(new String[]{"status"});
        
        assertFalse(app.isQuiet(), "Quiet mode should be disabled by default");
        String output = outContent.toString();
        assertTrue(output.contains("On branch"), "Status output should contain branch information");
        assertTrue(output.contains("working tree clean"), "Status output should contain working tree status");
    }
    
    @Test
    void testQuietModeAdd() {
        GitJava app = new GitJava();
        app.run(new String[]{"--quiet", "add", "file.txt"});
        
        assertTrue(app.isQuiet(), "Quiet mode should be enabled");
        assertEquals("", outContent.toString(), "No output should be produced in quiet mode for add");
    }
    
    @Test
    void testVerboseModeAdd() {
        GitJava app = new GitJava();
        app.run(new String[]{"add", "file.txt"});
        
        assertFalse(app.isQuiet(), "Quiet mode should be disabled by default");
        String output = outContent.toString();
        assertTrue(output.contains("Adding files"), "Add output should contain file information");
        assertTrue(output.contains("file.txt"), "Add output should contain the filename");
    }
    
    @Test
    void testQuietModeCommit() {
        GitJava app = new GitJava();
        app.run(new String[]{"-q", "commit", "-m", "Test commit"});
        
        assertTrue(app.isQuiet(), "Quiet mode should be enabled");
        assertEquals("", outContent.toString(), "No output should be produced in quiet mode for commit");
    }
    
    @Test
    void testVerboseModeCommit() {
        GitJava app = new GitJava();
        app.run(new String[]{"commit", "-m", "Test commit"});
        
        assertFalse(app.isQuiet(), "Quiet mode should be disabled by default");
        String output = outContent.toString();
        assertTrue(output.contains("[main"), "Commit output should contain branch information");
        assertTrue(output.contains("Test commit"), "Commit output should contain commit message");
    }
    
    @Test
    void testUsageNotShownInQuietMode() {
        GitJava app = new GitJava();
        app.run(new String[]{"--quiet"});
        
        assertTrue(app.isQuiet(), "Quiet mode should be enabled");
        assertEquals("", outContent.toString(), "Usage should not be shown in quiet mode");
    }
    
    @Test
    void testUsageShownInVerboseMode() {
        GitJava app = new GitJava();
        app.run(new String[]{});
        
        assertFalse(app.isQuiet(), "Quiet mode should be disabled by default");
        String output = outContent.toString();
        assertTrue(output.contains("Usage:"), "Usage should be shown when no command provided");
        assertTrue(output.contains("--quiet"), "Usage should mention quiet flag");
    }
    
    @Test
    void testSetQuietMethod() {
        GitJava app = new GitJava();
        
        assertFalse(app.isQuiet(), "Quiet mode should be disabled initially");
        
        app.setQuiet(true);
        assertTrue(app.isQuiet(), "Quiet mode should be enabled after setQuiet(true)");
        
        app.setQuiet(false);
        assertFalse(app.isQuiet(), "Quiet mode should be disabled after setQuiet(false)");
    }
    
    @Test
    void testQuietFlagBeforeCommand() {
        GitJava app = new GitJava();
        app.run(new String[]{"-q", "add", "file1.txt", "file2.txt"});
        
        assertTrue(app.isQuiet(), "Quiet mode should work when flag is before command");
        assertEquals("", outContent.toString(), "No output in quiet mode");
    }
    
    @Test
    void testErrorMessagesStillShownInQuietMode() {
        GitJava app = new GitJava();
        app.run(new String[]{"--quiet", "add"});
        
        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Nothing specified"), "Error messages should still be shown in quiet mode");
    }
}
