//package com.engineer.android.mini.better.regex;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * javac .\MatterTest.java
 * <p>
 * java MatterTest
 */
public class MatterTest {
    public static void main(String[] args) {
        String emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[a-z]{2,6}";

        Pattern pattern = Pattern.compile(emailRegex);

        String email1 = "example@example.com";
        String email2 = "please contact us at example@example.com or bbc@136.com or cnn@kk.com";
        Matcher matcher1 = pattern.matcher(email1);
        Matcher matcher2 = pattern.matcher(email2);


        System.out.println("total match-1 " + matcher1.matches());
        System.out.println("total match-2 " + matcher2.matches());

        while (matcher2.find()) {
            String info = String.format(Locale.getDefault(), "find result %20s,start from %3d,end at %3d",
                    matcher2.group(), matcher2.start(), matcher2.end());
            System.out.println(info);
        }
    }
}
