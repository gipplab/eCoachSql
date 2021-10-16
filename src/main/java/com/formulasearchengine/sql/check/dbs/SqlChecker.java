package com.formulasearchengine.sql.check.dbs;

import com.facebook.presto.sql.tree.QuerySpecification;
import com.formulasearchengine.sql.check.dbs.pojos.Query;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.RowProcessor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

import static com.facebook.presto.sql.SqlFormatter.formatSql;
import static com.formulasearchengine.sql.check.dbs.ReferenceQuery.getQuerySpecification;
import static com.formulasearchengine.sql.check.dbs.ReferenceQuery.removeLimits;

public class SqlChecker extends BaseChecker{
    protected SortedMap<String, Query> refs;

    protected Connection con;
    private ResultSet rs;
    private ArrayList<String> colNames;
    private long lastRuntime = 0;

    protected boolean checkCounts() {
        final int jdbcRowCount = getJDBCRowCount();
        final Integer expRowCount = refs.get(currentFile).rowCount;
        if (jdbcRowCount != expRowCount) {
            System.out.println("- Expected result with " + expRowCount + "rows but got " + jdbcRowCount + "rows.");
            return false;
        }
        return true;
    }

    protected boolean checkOrder(Map<String, Integer> orders) throws SQLException {
        Map<String, Double> last = new HashMap<>(orders.size());
        rs.first();
        for (String col : orders.keySet()) {
            final double v;
            v = getColAsDouble(col);
            last.put(col, v);
        }
        while (rs.next()) {
            for (Map.Entry<String, Integer> entry : orders.entrySet()) {
                final String key = entry.getKey();
                final Integer value = entry.getValue();
                final Double v = getColAsDouble(key);
                final Double lastVal = last.get(key);
                final int comp = v.compareTo(lastVal);
                if (comp != 0 && comp != value) {
                    System.out.println("- Order check failed. Comparison of '" + v + "' to '" + lastVal +
                            "' resulted in " + comp + " but " + value + " was expected.");
                    return false;
                }
                last.put(key, v);
            }
        }
        return true;
    }

    public boolean checkSchema() {
        List<String> expectedCols = refs.get(currentFile).columns;
        if (colNames.size() != expectedCols.size()) {
            System.out.println("- Expected " + expectedCols.size() + " columns but got " + colNames.size() +" columns.");
            return false;
        }
        for (String c : expectedCols) {
            if (!colNames.contains(c)) {
                System.out.println("- Column " + c + " missing in result schema. Maybe a spelling mistake?");
                return false;
            }
            final int realIndex = colNames.indexOf(c);
            final int expectedIndex = expectedCols.indexOf(c);
            if (realIndex != expectedIndex) {
                System.out.println(
                        "- Column " + c + " is expected be at position " + expectedIndex + " but was as " + realIndex);
                return false;
            }
        }

        return true;
    }

    public boolean hasError(String test) {
        setCurrentFile(test);
        return !loadFileContents() ||
                !runQuery() ||
                !getMetaData() ||
                !checkSchema() ||
                !checkCounts() ||
                !compareReference(false);
    }

    protected boolean compareReference(boolean order) {
        try {
            final int compareQueries;
            final long l = System.nanoTime();
            final String referenceQuery = refs.get(currentFile).stmt;
            if (refs.get(currentFile).limits) {
                final QuerySpecification querySpecification = getQuerySpecification(referenceQuery);
                final String noLimitSql = formatSql(removeLimits(querySpecification), Optional.empty());
                compareQueries = QueryComp.compareLimitedQueries(getStatement(), currentFileContent, noLimitSql);
            } else {
                compareQueries = QueryComp.compareQueries(order, getStatement(), currentFileContent, referenceQuery);
            }
            lastRuntime = System.nanoTime() - l;
            if (compareQueries != 0) {
                System.out.println("- Student solution and reference solution differ!");
                if (SHOW_DEBUG){
                    System.out.println("First row of student result set " + getFirstRow(currentFileContent));
                    System.out.println("First row of reference result set " + getFirstRow(referenceQuery));
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("- - - Internal error. Contact DB admin. Attach stack trace above.");
            return false;
        }

        return true;
    }

    private String getFirstRow(String q) throws SQLException {
        final Statement statement = getStatement();
        statement.execute(q);
        final ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        RowProcessor rp = new BasicRowProcessor();
        Map<String, Object> m = rp.toMap(resultSet);
        return m.toString();
    }

    private double getColAsDouble(String col) throws SQLException {
        double v;
        int fieldIndex = refs.get(currentFile).columns.indexOf(col) + 1; //JDBCs column index starts with 1
        if (fieldIndex == 0) {
            throw new SQLException("Column " + col + " not part of the result schema");
        }
        final int columnType = rs.getMetaData().getColumnType(fieldIndex);
        if (columnType == Types.DATE) {
            final Date date = rs.getDate(col);
            v = date.getTime();
        } else {
            v = rs.getDouble(col);
        }
        return v;
    }

    public ArrayList<String> getColNames() {
        return colNames;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public String getCurrentStatement() {
        return currentFileContent;
    }

    public String getFormattedRuntime() {
        return String.format("+ check took %1$.3f ms", getLastRuntime() / 1000000.);
    }

    public int getJDBCRowCount() {
        try {
            if (rs.last()) {
                return rs.getRow();
            } else {
                // the result set is empty
                return 0;
            }
        } catch (SQLException e) {
            printException(e);
            return -1;
        }
    }

    /**
     * Gets the runtime of the last comparison in ns
     *
     * @return long
     */
    public long getLastRuntime() {
        return lastRuntime;
    }

    public boolean getMetaData() {
        final ResultSetMetaData metaData;
        try {
            metaData = rs.getMetaData();
            final int columnCount = metaData.getColumnCount();
            colNames = new ArrayList<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                colNames.add(metaData.getColumnName(i).toUpperCase());
            }
        } catch (SQLException e) {
            printException(e);
            return false;
        }
        return true;
    }

    public double getPoints() {
        return points;
    }

    private Statement getStatement() throws SQLException {
        return con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    public double getTotalPoints() {
        return refs.size();
    }

    public boolean loadFileContents() {
        return loadFileContents("");
    }

    public boolean loadFileContents(String suffix) {
        return loadFileContents(suffix,".sql");
    }

    protected void printException(Exception e) {
        System.out.println(e.getMessage());
    }

    public void run() {
        System.out.println("\n\n======= Test Details ======");
        for (String test : refs.keySet()) {
            System.out.println("\nEXERCISE \"" + test + "\"");
            if (hasError(test)) {
                continue;
            }
            points++;
            System.out.println("+");
        }
        System.out.println("\n======== Test Report ======");
        if (points < refs.size()) {
            System.out.println("FAILURES!!! Some tests have errors. Correct them to achieve the maximum grade.");
        } else {
            System.out.println("No tests have errors.");
        }
        System.out.println("POINTS: " + getPoints() + "/" + getTotalPoints());
        System.out.println("===========================\n");
    }

    public boolean runQuery() {
        try {
            Statement st = getStatement();
            rs = st.executeQuery(currentFileContent);
        } catch (SQLException e) {
            printException(e);
            return false;
        }
        return true;
    }

    public boolean setCurrentFile(String currentFile) {
        if (refs.containsKey(currentFile)) {
            this.currentFile = currentFile;
            return true;
        } else {
            return false;
        }
    }
}
