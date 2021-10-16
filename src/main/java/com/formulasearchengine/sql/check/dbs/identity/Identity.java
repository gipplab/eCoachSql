package com.formulasearchengine.sql.check.dbs.identity;

import com.formulasearchengine.sql.check.dbs.BaseChecker;

import java.util.TreeMap;

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
        try {
            loadFileContents("a",".txt");
        } catch (Exception e) {
            feedback("Can not open your 1a.txt file " + e.getLocalizedMessage());
            return;
        }
    }
}
