package com.formulasearchengine.sql.check.dbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class QueryComp {

    static int compareQueries(Boolean sorted, Statement statement, String solutionQuery, String referenceQuery) {

        int actualHash;
        int expectedHash;

        try {
            actualHash = getHash(sorted, statement, solutionQuery);
        } catch (SQLException sqlE) {
            System.err.println(sqlE.getMessage());
            return 2;
        }
        try {
            expectedHash = getHash(sorted, statement, referenceQuery);
        } catch (SQLException sqlE) {
            throw new RuntimeException("Reference query 01 failed! Check Tests!", sqlE);
        }

        return actualHash == expectedHash ? 0 : 1;
    }

    static int compareLimitedQueries(Statement statement, String solutionQuery, String referenceQuery) {

        Set<Integer> actualHash;
        Set<Integer> expectedHash;

        try {
            actualHash = getHashes(statement, solutionQuery);
        } catch (SQLException sqlE) {
            System.err.println(sqlE.getMessage());
            return 2;
        }
        try {
            expectedHash = getHashes(statement, referenceQuery);
        } catch (SQLException sqlE) {
            throw new RuntimeException("Reference query 01 failed! Check Tests!", sqlE);
        }
        actualHash.removeAll(expectedHash);

        return actualHash.size() == 0 ? 0 : 1;
    }

    private static int getHash(Boolean sorted, Statement statement, String solutionQuery) throws SQLException {
        ResultSet actual;
        int actualHash;
        statement.setQueryTimeout(1700);
        statement.execute(solutionQuery);
        actual = statement.getResultSet();
        if (sorted) {
            actualHash = computeOrderAwareResultSetHash(actual);
        } else {
            actualHash = computeOrderTolerantResultHashes(actual).hashCode();
        }
        actual.close();
        return actualHash;
    }

    public static int computeOrderAwareResultSetHash(ResultSet result) throws SQLException {

        int hash;
        int rowCnt = 0;

        result.first();
        hash = computeRowHash(result) | rowCnt++;

        while (result.next()) {
            //noinspection NumericOverflow
            hash ^= (1315423911 ^ ((1315423911 << 5) + (computeRowHash(result) | rowCnt++) + (1315423911 >> 2)));
        }

        return hash;
    }

    public static Set<Integer> computeOrderTolerantResultHashes(ResultSet result) throws SQLException {
        HashSet<Integer> results = new HashSet<>();
        result.first();
        results.add(computeRowHash(result));

        while (result.next()) {
            results.add(computeRowHash(result));
        }

        return results;
    }

    private static int computeRowHash(ResultSet result) throws SQLException {

        int hash = 0;
        for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
            if ("java.sql.Array".equals(result.getMetaData().getColumnClassName(i))) {
                final ResultSet resultSet = result.getArray(i).getResultSet();
                //noinspection NumericOverflow
                hash ^= (1315423911 ^ ((1315423911 << 5) + computeOrderAwareResultSetHash(resultSet)+ (1315423911 >> 2)));
            } else {
                final Object o = result.getObject(i);
                if (o != null) { // ignore null values
                    //noinspection NumericOverflow
                    hash ^= (1315423911 ^ ((1315423911 << 5) + o.hashCode() + (1315423911 >> 2)));
                }
            }
        }
        return hash;
    }

    public static Connection getConnection() throws SQLException {
        return getConnection("public");
    }

    public static Connection getConnection(String schema) throws SQLException {
        String url = "jdbc:postgresql://localhost:54321/exampleDatabase?currentSchema=" + schema;
        Properties props = new Properties();
        props.setProperty("user", "exampleUser");
        props.setProperty("password", "examplePassword");
        return DriverManager.getConnection(url, props);
    }

    private static Set<Integer> getHashes(Statement statement, String solutionQuery) throws SQLException {
        ResultSet actual;
        statement.setQueryTimeout(1700);
        statement.execute(solutionQuery);
        actual = statement.getResultSet();

        final Set<Integer> resultHashes = computeOrderTolerantResultHashes(actual);

        actual.close();
        return resultHashes;
    }
}
