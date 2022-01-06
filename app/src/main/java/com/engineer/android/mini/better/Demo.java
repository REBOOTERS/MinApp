package com.engineer.android.mini.better;

public class Demo {
    public static void main(String[] args) {

    }

    private Demo() {
    }

    private static volatile Demo sDemo;

    public static Demo test() {
        if (sDemo == null) {
            synchronized (Demo.class) {
                if (sDemo == null) {
                    sDemo = new Demo();
                }
            }
        }
        return sDemo;
    }
}
