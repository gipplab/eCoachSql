package com.formulasearchengine.sql.check.dbs.csv;

import com.opencsv.bean.CsvBindAndSplitByPosition;

import java.util.Objects;
import java.util.SortedSet;

public class KeySet {
    @CsvBindAndSplitByPosition(position = 0, elementType = Integer.class, required = true)
    private SortedSet<Integer> elements;


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeySet){
            return elements.equals(((KeySet)obj).elements);
        } else {
            return super.equals(obj);
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
