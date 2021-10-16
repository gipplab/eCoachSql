package com.formulasearchengine.sql.check.dbs.pcstore;

import com.formulasearchengine.sql.check.dbs.SqlChecker;
import com.formulasearchengine.sql.check.dbs.Praktomat;
import com.formulasearchengine.sql.check.dbs.QueryComp;
import com.formulasearchengine.sql.check.dbs.pojos.Query;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeMap;

public class Seq1 extends SqlChecker {


    public Seq1(Path folder) throws SQLException {
        con = QueryComp.getConnection("exampleDatabase");
        testFolder = folder;
        final String[] EXPECTED_FILES = {"1", "2", "3"};
        refs = new TreeMap<>();
        for (String f : EXPECTED_FILES) {
            final InputStream sqlStream = Seq1.class.getClassLoader().getResourceAsStream(
                    "pcstore/1/" + f + ".json");
            final Query query = Query.fromJSON(convertStreamToString(sqlStream));
            refs.put(query.currentFile, query);
        }
    }


    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void main(String[] args) {
        String dir = Praktomat.getDir(args);
        final Path path = Paths.get(dir);
        try {
            final SqlChecker checker = new Seq1(path);
            checker.run();
        } catch (Exception e) {
            Praktomat.handleException(e);
        }
    }
}
