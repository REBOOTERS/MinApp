package com.engineer.android.mini.better;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class Demo {
    public static void main(String[] args) {
        Observable.intervalRange(0L, 200L, 0, 1L, TimeUnit.SECONDS)
//                .observeOn(Schedulers.computation())
                .doOnNext(aLong -> {
                    int m = Calendar.getInstance().get(Calendar.MINUTE);
                    int s = Calendar.getInstance().get(Calendar.SECOND);
                    System.out.printf("%d,%s:%s\n", aLong, m, s);
                })
                .doOnComplete(() -> System.out.println("complete")).subscribe();

        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
