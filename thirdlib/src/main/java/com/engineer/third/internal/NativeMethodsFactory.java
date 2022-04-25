package com.engineer.third.internal;

/**
 * Created on 2022/4/25.
 *
 * @author rookie
 */
public class NativeMethodsFactory {

    public native int plus(int a, int b);

    public static native int staticPlus(int a, int b);

    public native String transToNativeString(String input);
}
