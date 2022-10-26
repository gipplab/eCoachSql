package com.formulasearchengine.sql.check.dbs.csv;

import com.opencsv.bean.CsvBindByName;

import java.util.Objects;

@SuppressWarnings("unused")
public class Courses {

    // Schema given: CourseName,CourseNr,CreditHours,Department

    @CsvBindByName(required = true)
    private String courseName;

    @CsvBindByName(required = true)
    private String courseNr;

    @CsvBindByName(required = true)
    private Integer creditHours;

    public enum Department {
        CS, MATH
    }

    @CsvBindByName(required = true)
    private Department department;

    @Override
    public int hashCode() {
        return Objects.hash(courseName, courseNr, creditHours, department);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Courses){
            return hashCode()== obj.hashCode();
        } else {
            return super.equals(obj);
        }

    }

    @Override
    public String toString() {

        return "CourseName '" + courseName +
                "' CourseNr '" + courseNr +
                "' CreditHours '" +creditHours +
                "' Department '" +creditHours + "'";
    }

}
