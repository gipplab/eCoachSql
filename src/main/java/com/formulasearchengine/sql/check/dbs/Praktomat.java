package com.formulasearchengine.sql.check.dbs;

/**
 * Created by Moritz on 02.06.2017.
 */
public class Praktomat {

    public static void handleException(Exception e) {
        System.out.println("FAILURES!!! Details:");
        System.out.println(e.getMessage());
        if (e.getMessage() == null) {
            e.printStackTrace();
        }
    }

    public static String getDir(String[] args) {
        String dir;
        if (args.length > 0) {
            dir = args[0];
        } else {
            dir = System.getProperty("user.dir");
        }
        return dir;
    }
}
