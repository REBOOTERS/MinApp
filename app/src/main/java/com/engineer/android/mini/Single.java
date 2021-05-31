package com.engineer.android.mini;

public class Single {
    private Single() {
    }

    private static Single sSingle;

    public static Single getInstance() {
        if (sSingle == null) {
            synchronized (Single.class) {
                if (sSingle == null) {
                    sSingle = new Single();
                }
            }
        }
        return sSingle;
    }

    public void doo() {
        synchronized (this) {

        }
    }

    public synchronized int add1() {
        return 1 + 1;
    }
}
