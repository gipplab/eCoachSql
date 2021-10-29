package com.formulasearchengine.sql.check.dbs.csv;

import com.opencsv.bean.CsvBindAndSplitByName;

import java.util.SortedSet;

public class FunctionalDependency{
    @CsvBindAndSplitByName(required = true, elementType = Integer.class)
    private SortedSet<Integer> determinant;
    @CsvBindAndSplitByName(required = true, elementType = Integer.class)
    private SortedSet<Integer> dependant;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionalDependency){
            return determinant.equals(((FunctionalDependency)obj).determinant) &&
                    dependant.equals(((FunctionalDependency)obj).dependant);
        } else {
            return super.equals(obj);
        }

    }
}
