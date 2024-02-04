package com.example.carpool;

import java.util.ArrayList;

public class Student extends User{
    private String graduatingYear;
    private ArrayList<String> parentUIDs;

    public String getGraduatingYear() {
        return graduatingYear;
    }
    public void setGraduatingYear(String graduatingYear) {
        this.graduatingYear = graduatingYear;
    }
    public ArrayList<String> getParentUIDs() {
        return parentUIDs;
    }
    public void setParentUIDs(ArrayList<String> parentUIDs) {
        this.parentUIDs = parentUIDs;
    }
}
