package com.formulasearchengine.sql.check.dbs.pojos;

import com.google.gson.Gson;

import java.util.List;

public class Query {
    public Integer rowCount;
    public String stmt;
    public String currentFile;
    public List<String> columns;
    public Boolean limits = false;

    public Query() {
    }

    public static Query fromJSON(String json) {
        return new Gson().fromJson(json, Query.class);
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}