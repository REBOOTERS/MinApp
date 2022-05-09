package com.engineer.android.mini.util.model;

public class PeopleJava {
    public String name;
    public String address;


    public PeopleJava(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public PeopleJava() {
    }

    @Override
    public String toString() {
        return "PeopleJava{"
                + "name='" + name + '\''
                + ", address='" + address + '\''
                + '}';
    }
}
