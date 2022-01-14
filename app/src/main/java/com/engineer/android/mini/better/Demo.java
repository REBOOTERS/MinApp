package com.engineer.android.mini.better;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class Demo {
    private static Disposable sDisposable;

    public static void main(String[] args) {

        Observable.intervalRange(0L, 10L, 0, 1L, TimeUnit.SECONDS)
//                .observeOn(Schedulers.computation())
                .doOnNext(aLong -> {
                    int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    int m = Calendar.getInstance().get(Calendar.MINUTE);
                    int s = Calendar.getInstance().get(Calendar.SECOND);
                    System.out.printf("%d,%s:%s:%s\n", aLong, h, m, s);

                    if (aLong == 5) {
                        if (sDisposable != null) {
                            sDisposable.dispose();
                        }
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        sDisposable = disposable;
                    }
                })
                .doOnComplete(() -> System.out.println("complete")).subscribe();

        try {
            Thread.sleep(11000);
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
