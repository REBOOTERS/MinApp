package com.engineer.android.mini.proguards;

/**
 * Created on 2020/8/22.
 *
 * @author rookie
 */
public class C {
    public String a;

    public void aaa(String a, String b) {
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                System.out.println(a);
            } else {
                System.out.println(b);
            }
        }
    }

    public void mmm() {
        System.out.println(System.class);
    }
}
