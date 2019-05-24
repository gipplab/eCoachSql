package com.facebook.presto.sql;

import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.tree.Node;
import com.facebook.presto.sql.tree.Query;
import com.facebook.presto.sql.tree.QuerySpecification;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Moritz on 22.05.2017.
 */
public class SqlFormatterTest {
    private static String TESTQUERY = "SELECT a,b from T limit 10";
    private static String TESTQUERYII = "SELECT a, (SELECT a from T limit 1) b from T limit 10";

    @Test
    public void basicSql() throws Exception {
        SqlParser parser = new SqlParser();
        Query query = (Query) parser.createStatement(TESTQUERY);
        final QuerySpecification root = (QuerySpecification) query.getChildren().get(0);
        final Query noLimitQuery;
        assertEquals("SELECT\n"
                + "  \"a\"\n"
                + ", \"b\"\n"
                + "FROM\n"
                + "  \"T\"\n"
                + "LIMIT 10\n", SqlFormatter.formatSql(root, Optional.empty()));
        if (query.getLocation().isPresent()) {
            //noinspection OptionalGetWithoutIsPresent
            final QuerySpecification noLimitSepc = new QuerySpecification(
                    root.getLocation().get(),
                    root.getSelect(),
                    root.getFrom(),
                    root.getWhere(),
                    root.getGroupBy(),
                    root.getHaving(),
                    root.getOrderBy(),
                    Optional.empty());
            noLimitQuery = new Query(query.getLocation().get(), query.getWith(), noLimitSepc, query.getOrderBy(), Optional.empty());
        } else {
            noLimitQuery = new Query(query.getWith(), query.getQueryBody(), query.getOrderBy(), Optional.empty());
        }
        assertEquals("SELECT\n"
                + "  \"a\"\n"
                + ", \"b\"\n"
                + "FROM\n"
                + "  \"T\"\n", SqlFormatter.formatSql(noLimitQuery, Optional.empty()));
    }

    @Test
    @Ignore
    public void basicSqlII() throws Exception {
        SqlParser parser = new SqlParser();
        Query query = (Query) parser.createStatement(TESTQUERYII);
        final Node root = query.getChildren().get(0);
        final Query noLimitQuery;
        if (query.getLocation().isPresent()) {
            //noinspection OptionalGetWithoutIsPresent
            noLimitQuery = new Query(query.getLocation().get(), query.getWith(), query.getQueryBody(), query.getOrderBy(), Optional.empty());
        } else {
            noLimitQuery = new Query(query.getWith(), query.getQueryBody(), query.getOrderBy(), Optional.empty());
        }
        assertEquals("SELECT\n"
                + "  \"a\"\n"
                + ", (SELECT \"a\"\n"
                + "FROM\n"
                + "  \"T\"\n"
                + "LIMIT 1\n"
                + ") \"b\"\n"
                + "FROM\n"
                + "  \"T\"\n"
                + "LIMIT 10\n", SqlFormatter.formatSql(root, Optional.empty()));
        assertEquals("SELECT\n"
                + "  \"a\"\n"
                + ", \"b\"\n"
                + "FROM\n"
                + "  \"T\"\n", SqlFormatter.formatSql(noLimitQuery, Optional.empty()));
    }
}