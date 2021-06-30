package com.engineer.android.mini.better;

public class Demo {
    public static void main(String[] args) {

    }

    private Demo() {
    }

    private static volatile Demo mDemo;

    public Demo test() {
        if (mDemo == null) {
            synchronized (this) {
                if (mDemo == null) {
                    mDemo = new Demo();
                }
            }
        }
        return mDemo;
    }
}
