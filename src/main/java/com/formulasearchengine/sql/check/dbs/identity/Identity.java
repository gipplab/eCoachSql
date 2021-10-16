package com.formulasearchengine.sql.check.dbs.identity;

import com.formulasearchengine.sql.check.dbs.BaseChecker;

import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class Identity extends BaseChecker {

    public static void main(String[] args) {
        new Identity().go(args);
        System.exit(0);
    }
    @Override
    public void run() {
        refs = new TreeMap<>();
        currentFile = "1";
        testFolder = solution.location.toPath();
        feedback("\n\n======= Test Details ======");
        feedback("check if 1a.txt file exists");
        try {
            loadFileContents("a",".txt");
        } catch (Exception e) {
            feedback("Can not open your 1a.txt file " + e.getLocalizedMessage());
            return;
        }
        points++;
        feedback("+");
        feedback("check that file contains your name");
        try {
            assertThat("given name", currentFileContent, containsString(solution.givenName));
            assertThat("surname", currentFileContent, containsString(solution.sn));
        } catch (AssertionError | Exception e) {
            feedback("1a.txt does not contain your " + e.getLocalizedMessage());
            return;
        }
        points++;
        feedback("+");
    }
}
