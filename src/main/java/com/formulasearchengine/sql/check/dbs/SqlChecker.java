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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

import static com.facebook.presto.sql.SqlFormatter.formatSql;
import static com.formulasearchengine.sql.check.dbs.ReferenceQuery.getQuerySpecification;
import static com.formulasearchengine.sql.check.dbs.ReferenceQuery.removeLimits;

public class SqlChecker extends BaseChecker {
    protected SortedMap<String, Query> refs;

    protected Connection con;
    private ResultSet rs;
    private ArrayList<String> colNames;
    private long lastRuntime = 0;

    protected boolean checkCounts() {
        final int jdbcRowCount = getJDBCRowCount();
        final Integer expRowCount = refs.get(currentFile).rowCount;
        if (jdbcRowCount != expRowCount) {
            feedback("- Expected result with " + expRowCount + "rows but got " + jdbcRowCount + "rows.");
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
                final double v = getColAsDouble(key);
                final Double lastVal = last.get(key);
                final int comp = lastVal.compareTo(v);
                if (comp != 0 && comp != value) {
                    feedback("- Order check failed. Comparison of '" + v + "' to '" + lastVal +
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
            feedback("- Expected " + expectedCols.size() + " columns but got " + colNames.size() + " columns.");
            return false;
        }
        for (String c : expectedCols) {
            if (!colNames.contains(c)) {
                feedback("- Column " + c + " missing in result schema. Maybe a spelling mistake?");
                return false;
            }
            final int realIndex = colNames.indexOf(c);
            final int expectedIndex = expectedCols.indexOf(c);
            if (realIndex != expectedIndex) {
                feedback(
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
        final int compareQueries;
        final String referenceQuery = refs.get(currentFile).stmt;
        try {
            final long l = System.nanoTime();
            if (refs.get(currentFile).limits) {
                final QuerySpecification querySpecification = getQuerySpecification(referenceQuery);
                final String noLimitSql = formatSql(removeLimits(querySpecification), Optional.empty());
                compareQueries = QueryComp.compareLimitedQueries(getStatement(), currentFileContent, noLimitSql);
            } else {
                compareQueries = QueryComp.compareQueries(order, getStatement(), currentFileContent, referenceQuery);
            }
            lastRuntime = System.nanoTime() - l;
        } catch (SQLException e) {
            e.printStackTrace();
            feedback("- - - Internal error. Contact DB admin. Attach stack trace above.");
            return false;
        }
        if (compareQueries != 0) {
            feedback("- Student solution and reference solution differ!");
            if (SHOW_DEBUG) {
                try {
                    printDiff(currentFileContent, referenceQuery);
                } catch (SQLException e) {
                    e.printStackTrace();
                    feedback("- - - Internal error. Contact DB admin. Can not print diff between student and reference solution.");
                }
            }
            return false;
        }

        return true;
    }

    private void printDiff(String actual, String expected) throws SQLException {
        final Set<Map<String, Object>> actualRows = new HashSet<>();
        final Set<Map<String, Object>> expectedRows = new HashSet<>();

        Statement statement = getStatement();
        statement.execute(actual);
        final ResultSet resultSet = statement.getResultSet();
        statement = getStatement();
        statement.execute(expected);
        final ResultSet referenceSet = statement.getResultSet();
        RowProcessor rp = new BasicRowProcessor();

        while (resultSet.next() && referenceSet.next()) {
            actualRows.add(rp.toMap(resultSet));
            expectedRows.add(rp.toMap(referenceSet));
        }
        final Set<Map<String, Object>> superfluousRows = new HashSet<>(actualRows);
        superfluousRows.removeAll(expectedRows);
        expectedRows.removeAll(actualRows);
        feedback("Reference and result sets don't match.",
                "  The following rows are missing from the actual result: ",
                expectedRows.toString(),
                "  The following rows are superfluous:",
                superfluousRows.toString()
        );
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
        return loadFileContents(suffix, ".sql");
    }

    protected void printException(Exception e) {
        feedback(e.getMessage());
    }

    public void run() {
        feedback("\n\n======= Test Details ======");
        for (String test : refs.keySet()) {
            feedback("\nEXERCISE \"" + test + "\"");
            if (hasError(test)) {
                continue;
            }
            addCheckMark();
            points += getMaxPoints(test);
        }
    }

    public double getMaxPoints(String test) {
        return 1.0;
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
