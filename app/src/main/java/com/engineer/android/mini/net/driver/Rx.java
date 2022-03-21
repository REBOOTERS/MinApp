package com.engineer.android.mini.net.driver;

import static com.engineer.android.mini.coroutines.old.CoroutinesPlaygroudKt.log;

import org.reactivestreams.Subscription;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.schedulers.Schedulers;


public class Rx {

    private static Flowable sFlow;

    private static Other sOther = new Other();

    public static void main(String[] args) {
        System.out.println("Rx");
//        testMap();
//        testFor();
//        testRxFlow();
//        System.out.println(testReturnFinal());
        testInterval();
    }

    private static long start = 0L;

    private static void testInterval() {
        log("step-1");
//        long start = System.currentTimeMillis();
        Observable.intervalRange(0L, 11L, 0, 1L, TimeUnit.SECONDS)
                .doOnComplete(() -> {
                    log("diff = " + (System.currentTimeMillis() - start));
                    log("complete");
                })
                .doOnNext(aLong -> {
                    if (aLong == 0) {
                        start = System.currentTimeMillis();
                    }
                    log("along == " + aLong);
                }).subscribe();
        log("step-2");
        waitXSeconds(12);
        log("step-3");
    }

    private static void testMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("bar", "baz");
        map.put("11", "china");
        map.put("12", "china");
        map.put("012", "china");
        map.put("ABC", "china");

        for (String s : map.keySet()) {
            System.out.println(s + " : " + map.get(s));
        }

        Set<String> set = new HashSet<>();
        set.add("foo");
        set.add("bar");
        set.add("11");
        set.add("12");
        set.add("012");
        set.add("ABC");
        System.out.println(set);

        Set<String> treeSet = new TreeSet<>();
        treeSet.add("foo");
        treeSet.add("bar");
        treeSet.add("11");
        treeSet.add("12");
        treeSet.add("012");
        treeSet.add("ABC");
        System.out.println(treeSet);

    }

    private static int testReturnFinal() {

        try {
            System.out.println(11111);
//            System.out.println(1/0);// make a exception
//            System.exit(0); // final will execute ?
            return 1;
        } catch (Exception e) {
            System.out.println(33333);
        } finally {
            System.out.println(22222);
        }
        return 2;
    }

    private static void testFor() {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int i : array) {
            if (i <= 3) {
                continue;
            }
            if (i > 7) {
                return;
            }
            System.out.println(i);
        }
    }

    private static void waitXSeconds(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testRxFlow() {
        createFlow();
        subscribeFlow();

        waitXSeconds(40);
    }


    private static void createFlow() {
        System.out.println("1");
        sFlow = Flowable.create((FlowableOnSubscribe<Integer>) emitter -> {
            System.out.println("here");

            Observable.just(1).delay(2, TimeUnit.SECONDS)
                    .doOnNext(l -> {
                        for (int i = 0; i < 10; i++) {
                            waitXSeconds(1);
                            emitter.onNext(i);
                            System.out.println("emitter " + i + ", " + Thread.currentThread().getName());
                        }
                    }).subscribe();
            emitter.onComplete();
        }, BackpressureStrategy.LATEST)
                .publish();

        ((ConnectableFlowable) sFlow).connect();
    }


    private static void subscribeFlow() {
        System.out.println("2");
        sFlow.observeOn(Schedulers.newThread())
                .subscribe(new FlowableSubscriber<Integer>() {


                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                        System.out.println("onSubscribe " + s);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        waitXSeconds(3);
                        System.out.println("onNext: " + integer + ", " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("onError " + t);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }

    static class Other {
        private MyCallback myCallback;

        private void setCallback(MyCallback myCallback) {
            this.myCallback = myCallback;
        }

        private void trigger() {
            if (myCallback != null) {
                myCallback.onCallback(110);
            }
        }

        public interface MyCallback {
            void onCallback(int num);
        }
    }
}