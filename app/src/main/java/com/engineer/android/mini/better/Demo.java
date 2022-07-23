package com.engineer.android.mini.better;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.engineer.android.mini.util.TimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class Demo {
    private static Disposable sDisposable;

    private static final AtomicBoolean S_INIT = new AtomicBoolean();

    public static void main(String[] args) {
        TimeZone china = TimeZone.getTimeZone("GMT+08:00");
        Calendar calendar = Calendar.getInstance(china);
        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);
        long curTime = calendar.getTimeInMillis();
        long sysTime = System.currentTimeMillis();
        System.out.println("curTime = " + curTime + ", date = " + TimeUtil.getTime(curTime));
        System.out.println("sysTime = " + sysTime + ", date = " + TimeUtil.getTime(sysTime));
        System.out.println("========");
        System.out.println("atomicBoolean current1 = " + S_INIT.get());
        System.out.println("atomicBoolean ==1 " + S_INIT.compareAndSet(false, true));
        System.out.println("atomicBoolean current2 = " + S_INIT.get());
        System.out.println("atomicBoolean ==2 " + S_INIT.compareAndSet(false, true));
        System.out.println("atomicBoolean current3 = " + S_INIT.get());

        testJson();

        double value = ((1.8 / 1000000 * 9000000 * 1.137 * 108));
        System.out.println("value is " + value);
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


    private static void testJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "mike");
        map.put("age", 12);
        map.put("address", "中国北京");

        Map<String, Object> result = new HashMap<>();
        result.put("result", map);
        result.put("level", 1);
        result.put("isVip", false);
        result.put("card", null);
        result.put("time", System.currentTimeMillis());


        String beauty = JSON.toJSONString(result,
                SerializerFeature.PrettyFormat,
                SerializerFeature.SortField,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        System.out.println(beauty);
    }


    private static void testInterval() {
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

    public static void testTryCatch() {
        try {
            test2();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
//            test1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void test1() {
        throw new VerifyError();
    }

    private static void test2() throws Exception {
        throw new Exception("null");
    }
}
