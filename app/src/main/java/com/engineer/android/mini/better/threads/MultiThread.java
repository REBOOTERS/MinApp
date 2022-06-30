package com.engineer.android.mini.better.threads;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiThread {

//    private static List<String> list = new ArrayList<>();
    private static List<String> list = new CopyOnWriteArrayList<>();

    static {
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            System.out.println(" execute " + i + " times ");
            new Thread(MultiThread::useList).start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private  static void useList() {
        list.forEach(System.err::print);
        System.out.print(",");
        list.clear();
    }

}
