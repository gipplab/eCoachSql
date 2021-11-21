package com.formulasearchengine.sql.check.dbs;

import com.facebook.presto.sql.SqlFormatter;
import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Query;
import com.facebook.presto.sql.tree.QuerySpecification;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Moritz on 11.05.2017.
 */
public class ReferenceQueryTest {
    private ReferenceQuery q;

    @Test
    public void removeLimitsTest() throws Exception {

        final String TESTQUERY = "SELECT a,b from T limit 10";
        // note that currenlty removeLimits only removes the limits from the outermost query
        // private static String TESTQUERYII = "SELECT a, (SELECT a from T limit 1) b from T limit 10";

        SqlParser parser = new SqlParser();
        Query query = (Query) parser.createStatement(TESTQUERY);
        final QuerySpecification root = (QuerySpecification) query.getChildren().get(0);
        assertEquals("SELECT\n"
                + "  a\n"
                + ", b\n"
                + "FROM\n"
                + "  \"T\"\n"
                + "LIMIT 10\n", SqlFormatter.formatSql(root, Optional.empty()));
        final QuerySpecification noLimitQuery = ReferenceQuery.removeLimits(root);
        assertEquals("SELECT\n"
                + "  a\n"
                + ", b\n"
                + "FROM\n"
                + "  \"T\"\n", SqlFormatter.formatSql(noLimitQuery, Optional.empty()));
    }

    @Test
    @Ignore
    public void generateJsonFiles() throws Exception {
        q.generateJsonFiles("pcstore/1");
    }

    @Test
    public void getColNames() throws Exception {
        assertEquals(q.getColNames().get(0), "ALBUM");
    }

    @Test
    public void getCurrentFile() throws Exception {
        assertEquals(q.getCurrentFile(), "3a");
    }

    @Test
    public void getRowCount() throws Exception {
        assertEquals(9, (int) q.getRowCount());
    }

    @Test
    public void getStmt() {
        assertThat(q.getStmt(), containsString("radiohead"));
    }

    @Before
    public void init() throws SQLException, IOException, URISyntaxException {
        q = new ReferenceQuery("hp03/1", "3a");
    }

    @Test
    public void jsonRoundTripTest() {
        final String json = q.toJSON();
        final ReferenceQuery q2 = new ReferenceQuery(json);
        assertEquals(json, q2.toJSON());
    }

    @Test
    public void toStringTest() throws Exception {
        assertThat(q.toJSON(), containsString("\"rowCount\""));
    }
}
