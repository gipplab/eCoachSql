package com.formulasearchengine.sql.check.dbs.identity;

import com.formulasearchengine.sql.check.dbs.BaseChecker;

import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class Identity extends BaseChecker {
    public static void main(String[] args) {
        if (args.length != 6) {
            throw new RuntimeException("6 arguments are mandatory");
        }
        new Identity().go(args);
    }

    @Override
    public void run() {
        refs = new TreeMap<>();
        currentFile = "1";
        testFolder = solution.location.toPath();
        feedback("\n\n======= Test Details ======");
        feedback("check if 1a.txt file exists");
        if (!loadFileContents("a", ".txt"))
            return;
        addCheckMark();
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
        addCheckMark();
        points++;
        feedback("+");
        feedback("check if 1b.txt file exists");
        if (!loadFileContents("b", ".txt"))
            return;
        addCheckMark();
        points++;
        feedback("+");
        feedback("check that file contains your student id");
        try {
            assertThat(currentFileContent, containsString(solution.userName));
        } catch (AssertionError | Exception e) {
            feedback("1b.txt does not contain your student id:" + e.getLocalizedMessage());
            return;
        }
        addCheckMark();
        points++;
        feedback("+");
    }

    @Override
    public double getTotalPoints() {
        return 4;
    }
}
