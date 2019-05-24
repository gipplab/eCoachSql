package com.formulasearchengine.sql.check;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Moritz on 28.04.2017.
 */
public class GenericTests {

    @Test
    public void dbsAccessTest() throws Exception {
        String url = "jdbc:mariadb://localhost:3308/exampleDatabase";
        Properties props = new Properties();
        props.setProperty("user", "exampleUser");
        props.setProperty("password", "examplePassword");
        Connection con = DriverManager.getConnection(url, props);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT VERSION()");

        if (rs.next()) {
            String version = rs.getString(1);
            assertThat("Check that it's a MariadDB database", version, containsString("maria"));
            System.out.println();
        } else {
            fail("SQL command failed");
        }
    }
}
