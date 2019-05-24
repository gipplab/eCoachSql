package com.formulasearchengine.sql.check.dbs;

import com.facebook.presto.sql.parser.SqlParser;
import com.facebook.presto.sql.parser.StatementSplitter;
import com.facebook.presto.sql.tree.QuerySpecification;
import com.formulasearchengine.sql.check.dbs.pojos.Query;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ReferenceQuery {

    private Query query;
    private ResultSet rs;

    public ReferenceQuery(String json) {
        this.query = Query.fromJSON(json);
    }

    public ReferenceQuery(String resourceName, String filename) throws SQLException, URISyntaxException, IOException {
        this(Paths.get(ReferenceQuery.class.getClassLoader().getResource(resourceName).toURI()), filename);
    }

    public ReferenceQuery(String resourceName, String filename, boolean force) throws SQLException, URISyntaxException, IOException {
        this(Paths.get(ReferenceQuery.class.getClassLoader().getResource(resourceName).toURI()), filename, force);
    }

    public ReferenceQuery(Path folder, String filename) throws SQLException, URISyntaxException, IOException {
        this(folder, filename, false);
    }

    public ReferenceQuery(Path folder, String filename, boolean force) throws SQLException, URISyntaxException, IOException {
        try {
            setQuery(folder, filename);
            if (force) {
                generateQueryFromSqlFile(folder, filename);
            }
        } catch (NoSuchFileException e) {
            generateQueryFromSqlFile(folder, filename);
        }
    }

    private static String loadFileContents(Path folder, String filename) throws IOException {
        final String searchFile = filename + ".sql";
        return new String(Files.readAllBytes(Paths.get(folder.toString(), searchFile)));
    }

    public void generateJsonFiles(String folder) throws IOException, URISyntaxException {
        final Path fld = Paths.get(ReferenceQuery.class.getClassLoader().getResource(folder).toURI());
        final Stream<Path> walk = Files.walk(fld);
        walk.forEach(f -> {
            if (f.getFileName().toString().endsWith(".sql")) {
                try {
                    final String fname = f.getFileName().toString().split("\\.")[0];
                    final ReferenceQuery referenceQuery = new ReferenceQuery(folder, fname, true);
                    final String json = referenceQuery.toJSON();
                    PrintStream out = new PrintStream(new FileOutputStream(
                            Paths.get(fld.toString(),
                                    fname + ".json").toString()));
                    out.print(json);
                    out.close();
                } catch (SQLException | URISyntaxException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Ignoring " + f.getFileName().toString());
            }
        });
    }

    void generateQueryFromSqlFile(Path folder, String filename) throws IOException, SQLException {
        query = new Query();
        query.currentFile = filename;
        query.stmt = loadFileContents(folder, filename);
        rs = getResultSet();
        ResultSetMetaData metaData = rs.getMetaData();
        final int columnCount = metaData.getColumnCount();
        query.columns = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            query.columns.add(metaData.getColumnName(i).toUpperCase());
        }
        query.rowCount = getJDBCRowCount();
        tryParse(query.stmt);
    }

    public List<String> getColNames() {
        return query.columns;
    }

    private Connection getConnection() throws SQLException {
        return QueryComp.getConnection("public");
    }

    public String getCurrentFile() {
        return query.currentFile;
    }

    private int getJDBCRowCount() throws SQLException {
        if (rs.last()) {
            return rs.getRow();
        } else {
            // the result set is empty
            return 0;
        }
    }

    public Query getQuery() {
        return query;
    }

    private ResultSet getResultSet() throws SQLException {
        Statement st = getStatement();
        return st.executeQuery(query.stmt);
    }

    public Integer getRowCount() {
        return query.rowCount;
    }

    private Statement getStatement() throws SQLException {
        return getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    public String getStmt() {
        return query.stmt;
    }

    static QuerySpecification removeLimits(QuerySpecification q) {
        if (q.getLocation().isPresent()) {
            //noinspection OptionalGetWithoutIsPresent
            return new QuerySpecification(
                    q.getLocation().get(),
                    q.getSelect(),
                    q.getFrom(),
                    q.getWhere(),
                    q.getGroupBy(),
                    q.getHaving(),
                    q.getOrderBy(),
                    Optional.empty());
        } else {
            return new QuerySpecification(
                    q.getSelect(),
                    q.getFrom(),
                    q.getWhere(),
                    q.getGroupBy(),
                    q.getHaving(),
                    q.getOrderBy(),
                    Optional.empty());
        }
    }

    private void setQuery(Path folder, String filename) throws IOException {
        final String searchFile = filename + ".json";
        final String json = new String(Files.readAllBytes(Paths.get(folder.toString(), searchFile)));
        query = Query.fromJSON(json);
    }

    public String toJSON() {
        return query.toJSON();
    }

    private void tryParse(String sql) {
        try {
            final QuerySpecification q = getQuerySpecification(sql);
            query.limits = q.getLimit().isPresent();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static QuerySpecification getQuerySpecification(String sql) {
        SqlParser parser = new SqlParser();
        final List<StatementSplitter.Statement> stmts = new StatementSplitter(sql).getCompleteStatements();
        final String firstStatement;
        switch (stmts.size()) {
            case 1:
                firstStatement = stmts.get(0).statement();
                break;
            case 0:
                firstStatement = sql;
                System.out.println("Notice: No complete statements found.");
                break;
            default:
                firstStatement = stmts.get(0).statement();
                System.out.println("Warning: More than one statement in reference solution");
        }
        com.facebook.presto.sql.tree.Statement stmt = parser.createStatement(firstStatement);
        return (QuerySpecification) stmt.getChildren().get(0);
    }
}
