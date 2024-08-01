package com.engineer.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Main {
    public static void main(String[] args) {
        System.out.println("hello module");

        testJson();
    }

    private static void testJson() {
        Student student = new Student();
        student.name = "mike";
        student.address = "beijing";
        student.age = 11;

        String s = JSON.toJSONString(student, SerializerFeature.PrettyFormat);
        System.out.println(s);

    }

    static class Student {
        public String name;
        public int age;
        public String address;
    }
}
