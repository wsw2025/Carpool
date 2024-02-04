package com.example.carpool;

import java.util.ArrayList;

public class Parent extends User{
    private ArrayList<String> childrenUIDs;

    public ArrayList<String> getChildrenUIDs() {
        return childrenUIDs;
    }
    public void setChildrenUIDs(ArrayList<String> childrenUIDs) {
        this.childrenUIDs = childrenUIDs;
    }
}
