package com.formulasearchengine.sql.check.dbs.identity;

import junit.framework.TestCase;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertThrows;

public class IdentityTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testMain() {
        final Path folder = getTestFolder("com.formulasearchengine.sql.check.good/identity/");

        String[] args = {
                folder.toString(),  // 0: env.tmpdir(),
                "1",                // 1: str(env.user().id),
                "123456",           // 2: str(env.user().mat_number),
                "first",            // 3: str(env.user().first_name),
                "last",             // 4: str(env.user().last_name),
                "42"                // 5: str(env.solution().id)]
        };
        Identity.main(args);
    }

    public void testMainTooFew() {
       assertThrows(RuntimeException.class, () -> {
                    String[] args = {};
                    Identity.main(args);
                }
        );
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