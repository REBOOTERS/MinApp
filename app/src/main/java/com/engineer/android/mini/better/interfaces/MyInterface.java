package com.engineer.android.mini.better.interfaces;

public interface MyInterface {
    void test();
}

interface Element extends MyInterface {
    void test1();
}

class TTT implements MyInterface {

    @Override
    public void test() {

    }

    void foo() {
        TTT ttt = new TTT();
        ttt.test();
    }
}