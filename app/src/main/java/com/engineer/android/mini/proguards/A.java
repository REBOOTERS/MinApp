package com.engineer.android.mini.proguards;

/**
 * Created on 2020/8/22.
 *
 * @author rookie
 */
public class A {
    private void test() {
        int max = 10;
        for (int i = 0; i < max; i++) {
            double value = Math.sqrt(i);
            System.out.println(value);

        }
        System.out.println("1");
    }

    public void test2() {
        test();
        System.out.println(2);
    }

    int m = 1;
}
