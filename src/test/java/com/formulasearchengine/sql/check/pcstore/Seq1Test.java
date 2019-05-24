package com.formulasearchengine.sql.check.pcstore;

import com.formulasearchengine.sql.check.dbs.ReferenceQuery;
import com.formulasearchengine.sql.check.dbs.pcstore.Seq1;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Seq1Test {
    private Seq1 c;
    private ReferenceQuery q;


    @Test
    public void runTest() throws Exception {
        c.run();
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

    @Before
    public void init() throws Exception {
        final Path folder = getTestFolder("com.formulasearchengine.sql.check.good/pcstore/1/");
        c = new Seq1(folder);
        q = new ReferenceQuery("pcstore/1", "1");
    }


    @Test
    public void generateJsonFiles() throws Exception {
        q.generateJsonFiles("pcstore/1");
    }
}

