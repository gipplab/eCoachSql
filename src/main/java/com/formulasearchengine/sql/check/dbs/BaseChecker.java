package com.formulasearchengine.sql.check.dbs;

import com.formulasearchengine.sql.check.dbs.pojos.Solution;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.lang.Math;


public abstract class BaseChecker {
    static final boolean SHOW_DEBUG = true;
    private PrintStream orgStream;
    protected Solution solution;
    protected Path testFolder;
    protected double points = 0.;
    protected SortedMap<String, Function<BaseChecker, Boolean>> refs;
    protected String currentFile;
    protected String currentFileContent;
    private final StringBuilder output = new StringBuilder();

    protected void feedback(String... log) {
        for (String s : log) {
            output.append("\n");
            output.append(s);
        }
    }

    protected void addCheckMark(){
        output.append(" ✓");
    }

    public String getFormattedRuntime() {
        return String.format("+ check took %1$.3f ms", getLastRuntime() / 1000000.);
    }

    /**
     * Gets the runtime of the last comparison in ns
     *
     * @return long
     */
    public long getLastRuntime() {
        return 0;
    }

    public double getPoints() {
        return points;
    }


    public double getTotalPoints() {
        return refs.size();
    }

    public boolean loadFileContents() {
        return loadFileContents("", ".sql");
    }

    public boolean loadFileContents(String suffix, String extension) {
        final String searchFile = currentFile + suffix + extension;
        try {
            currentFileContent = new String(Files.readAllBytes(Paths.get(
                    testFolder.toString(), searchFile)));
            return true;
        } catch (IOException e) {
            try {
                final List<Path> fileList = Files.walk(testFolder)
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList());
                feedback("- File '" + searchFile + "' not found. Your upload contains the following files:");
                for (Path path : fileList) {
                    feedback("\t" + path.getFileName());
                }
                feedback("\tNote that some OS treat filenames as cases sensitive.");
            } catch (IOException e1) {
                printException(e1);
            }
            printException(e);
        }
        return false;
    }

    protected void printException(Exception e) {
        feedback(e.getMessage());
    }

    public void writeOutput() {
        System.setOut(orgStream);
        try {
            feedback("\n======== Test Report ======");
            if (points < getTotalPoints()) {
                feedback("FAILURES!!! Some tests have errors. Correct them to achieve the maximum grade.");
            } else {
                feedback("No tests have errors.");
            }
            feedback("POINTS: " + getPoints() + "/" + getTotalPoints());
            feedback("===========================\n");
            String pointRecord = solution.solutionId + ", " + solution.sn + ", " + solution.givenName + ", " + Math.round( getTotalPoints() ) + ", " + Math.round( getPoints() ) + "\n" ;
            try {
                Files.write(Paths.get("/tmp/points.csv"), pointRecord.getBytes(), StandardOpenOption.APPEND);
            }catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private boolean hasError(String test) {
        return false;
    }


    public void handleException(Exception e) {
        feedback("FAILURES!!! Details:");
        feedback(e.getMessage());
        if (e.getMessage() == null) {
            e.printStackTrace();
        }
    }


    public void go(String[] args) {
        try {
            redirectStdOut();
            solution = new Solution(args);
            run();
            writeOutput();
        } catch (Exception e) {
            handleException(e);
        }
        flushLog();
    }

    private void flushLog() {
        System.out.println(output);
    }

    abstract public void run();

    private void redirectStdOut() {
        orgStream = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream myPrintStream;
        try {
            myPrintStream = new PrintStream(baos, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            myPrintStream = new PrintStream(baos);
        }
        System.setOut(myPrintStream);
    }
}