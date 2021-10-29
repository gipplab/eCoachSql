package com.formulasearchengine.sql.check.dbs.csv;

import com.opencsv.bean.AbstractCsvConverter;

import java.util.Locale;

public class LowerConverter extends AbstractCsvConverter  {

    @Override
    public String convertToRead(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
