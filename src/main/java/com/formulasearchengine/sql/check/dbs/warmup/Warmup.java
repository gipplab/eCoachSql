package com.formulasearchengine.sql.check.dbs.warmup;

import com.formulasearchengine.sql.check.dbs.SemiManualChecker;
import com.formulasearchengine.sql.check.dbs.csv.Courses;
import com.google.common.base.Strings;

import java.util.Arrays;

public class Warmup extends SemiManualChecker {

    public static void main(String[] args) {
        if (args.length != 6) {
            throw new RuntimeException("6 arguments are mandatory. Contact your system administrator.");
        }
        new Warmup().go(args);
    }

    @Override
    public void run() {
        currentFile = "2";
        testFolder = solution.location.toPath();
        feedback("\n\n======= Test Details ======");
        feedback("check if 2a.csv file exists");
        if (loadFileContents("a", ".csv"))
            compareWithResource(Warmup.class.getClassLoader().getResourceAsStream("warmup/2a.csv"),
                    1.,
                    Courses.class,
                    true);
        currentFileContent = "";
        feedback("check if 2b.txt file exists");
        if (loadFileContents("b", ".txt"))
            check2b();
        currentFileContent = "";
        feedback("check if 2c.csv file exists");
        if (loadFileContents("c", ".csv"))
            compareWithResource(Warmup.class.getClassLoader().getResourceAsStream("warmup/2c.csv"),
                    1.,
                    Courses.class,
                    true);
        currentFileContent = "";
    }
    private void check2b() {
        String[] lines = Arrays.stream(currentFileContent.split("\\R"))
                .filter(x -> !Strings.isNullOrEmpty(x))
                .toArray(String[]::new);
        feedback("Check number of rows");
        if (lines.length != 8) {
            feedback("Expecting 8 rows, but got" + lines.length);
            return;
        }
        addCheckMark();
        feedback("Check number of columns");
        if (!lines[0].replaceAll("-", "").equals("+++++")) {
            feedback("Number or columns does not match. Expecting header in the form " +
                    "`+---+----+---+---+`, but got "
                    + lines[0]);
            return;
        }
        addCheckMark();
        String[] patterns = {
                "\\|.*?Intro to Computer Science.*?\\|.*?CS1310.*?\\|.*?4 \\|.*?CS.*?\\|",
                "\\|.*?Data Structures.*?\\|.*?CS3320.*?\\|.*?4.*?\\|.*?CS.*?\\|",
                "\\|.*?Discrete Mathematics.*?\\|.*?MATH2410.*?\\|.*?3.*?\\| MATH.*?\\|",
                "\\|.*?Databases.*?\\|.*?CS3360.*?\\|.*?3.*?\\|.*?CS.*?\\|"
        };
        for (int i = 0; i < 4; i++) {
            feedback("Check record " + (i+1));
            if (!lines[i+3].matches(patterns[i])) {
                feedback("Record " + (i+1) +" does not match");
                return;
            }
            addCheckMark();
        }
        feedback("+");
        points++;
    }


    @Override
    public double getTotalPoints() {
        return 0;
    }
}
