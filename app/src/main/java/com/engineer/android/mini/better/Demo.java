package com.engineer.android.mini.better;

import android.os.Handler;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.engineer.android.mini.util.TimeUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
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
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(timeZone);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long time1 = calendar.getTimeInMillis();
        System.out.println(time1);


        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long time2 = calendar.getTimeInMillis();
        System.out.println(time2);

        long now = System.currentTimeMillis();
        System.out.println(now);
        System.out.println(now > time1);
        System.out.println(now < time2);

        long diff = time2 - time1;
        System.out.println(diff);
        System.out.println(diff / 60f);
        System.out.println(diff / 3600f);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println(dayOfWeek);

        /**
         * {"STORAGE":{"mode":"cloud_storage","type":""},
         * "TARGET_FOLLOW":"1","CRUISE_TIME":[{"cruiseTime":"08:00","cycle":
         * "0,1,2,3,4,5,6","status":"0","time":"0800","timestamp":
         * "1753488000000"},{"cruiseTime":"12:00","cycle":"0,1,2,3,4,5,6",
         * "status":"0","time":"1200","timestamp":"1753502400000"},
         * {"cruiseTime":"16:00","cycle":"0,1,2,3,4,5,6","status":"0"
         * ,"time":"1600","timestamp":"1753430400000"},{"cruiseTime":"15:10",
         * "cycle":"","status":"1","time":"1510","timestamp":"1753427400000"}],
         * "CRUISE_ANGLE":{"end":{"pan":"-73","tilt":"0"},"extent":"180",
         * "mode":"collect_point","start":{"pan":"61","tilt":"0"}},
         * "cuei":"200049040000396","switchStatus":"1"}
         */

        fileTime();
    }



    public static void fileTime() {
        Path filePath = Paths.get("Demo.java");

        try {
            // 获取基本文件属性
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);

            System.out.println("文件创建时间: " + attrs.creationTime());
            System.out.println("最后修改时间: " + attrs.lastModifiedTime());
            System.out.println("最后访问时间: " + attrs.lastAccessTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testAny() {
        testJson();

        double value = ((1.8 / 1000000 * 9000000 * 1.137 * 108));
        System.out.println("value is " + value);

        BigDecimal dd = new BigDecimal("11.0");
    }


    private static void testCalendar() {
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


        String beauty = JSON.toJSONString(result, SerializerFeature.PrettyFormat, SerializerFeature.SortField,
                SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
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
                }).doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        sDisposable = disposable;
                    }
                }).doOnComplete(() -> System.out.println("complete")).subscribe();

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

    private Runnable task = () -> Log.d("tag", "run() called");

    private void max(int a, int b) {
        new Handler().post(() -> {
            Log.d("tag", "111");
//            task;
            Log.d("tag", "222");
        });


        new Handler().post(task);
    }
}
