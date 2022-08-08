package com.engineer.android.mini.proguards;

import java.io.Serializable;

public class ModelA implements Serializable {

    public static final String TAG = "This is a ModelA Class";

    public String name_10000000_1000;
    public int age;
    private float point;

    public String getInfo() {
        return "name " + name_10000000_1000 + ",age " + age + ",point" + point;
    }

    public static class ModelB {
        public String add;
    }
}
