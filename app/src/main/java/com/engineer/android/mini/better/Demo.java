package com.engineer.android.mini.better;

public class Demo {
    public static void main(String[] args) {

    }

    private Demo() {
    }

    private static volatile Demo mDemo;

    public static Demo test() {
        if (mDemo == null) {
            synchronized (Demo.class) {
                if (mDemo == null) {
                    mDemo = new Demo();
                }
            }
        }
        return mDemo;
    }
}
