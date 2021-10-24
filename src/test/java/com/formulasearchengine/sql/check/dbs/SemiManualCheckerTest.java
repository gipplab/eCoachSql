package com.formulasearchengine.sql.check.dbs;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SemiManualCheckerTest {

    private SemiManualChecker instance;
    private String feedback ="";

    @Before
    public void setUp() throws Exception {
        instance = new SemiManualChecker() {
            @Override
            public void run() {
                testFolder= getTestFolder("com.formulasearchengine.sql.check.good/hp04/1/");
                currentFile = "1";
            }

            @Override
            protected void feedback(String... log) {
                for (String s : log) {
                    feedback+=s;
                    feedback+="\n";
                }
                super.feedback(log);
            }

            public double getMaybePoints() {
                return maybePoints;
            }

            public double getTotalMaybePoints() {
                return totalMaybePoints;
            }

        };
        instance.run();
    }

    @Test
    public void filenameExistsSuccess() {
        instance.filenameExists("1a.sql",2.);
        assertThat(feedback, CoreMatchers.containsString("2.0 points for this upload"));
    }

    @Test
    public void filenameExistsFail() {
        instance.filenameExists("missing",2.);
        assertThat(feedback, CoreMatchers.containsString("No solution for task missing submitted"));
    }

    @Test
    public void testWriteOutput() {
        instance.filenameExists("1a.sql",2.);
        instance.filenameExists("missing.sql",3.);

        instance.writeOutput();
        assertThat(feedback,CoreMatchers.containsString("2.0 of 5.0 additional points that will be granted by humans"));
    }

    private Path getTestFolder(String name) {
        URI testfolder = null;
        try {
            testfolder = getClass().getClassLoader().getResource(name).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }
        return Paths.get(testfolder);
    }
}