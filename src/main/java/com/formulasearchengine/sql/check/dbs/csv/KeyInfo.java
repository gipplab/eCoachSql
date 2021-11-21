package com.formulasearchengine.sql.check.dbs.csv;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;

import java.util.Objects;
import java.util.SortedSet;

@SuppressWarnings("unused")
public class KeyInfo {

    public enum Tables {
        mountain, encompasses
    }

    public enum Fields {
        area, continent, country, height, name
    }

    @CsvBindByName(required = true)
    private Tables table;
    @CsvBindByName(capture = " *([^ ]+).*", required = true)
    private Boolean candidateKey;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @CsvBindAndSplitByName(elementType = Fields.class, converter = LowerConverter.class, required = true)
    private SortedSet<Fields> columns;

    @Override
    public int hashCode() {
        return Objects.hash(table, candidateKey, columns);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeyInfo) {
            return table.equals(((KeyInfo) obj).table) &&
                    candidateKey == ((KeyInfo) obj).candidateKey &&
                    columns.equals(((KeyInfo) obj).columns);
        } else {
            return super.equals(obj);
        }

    }

    @Override
    public String toString() {
        return "table '" + table + "' candidateKey '" + candidateKey + "' columns '" + columns + "'";
    }
}
