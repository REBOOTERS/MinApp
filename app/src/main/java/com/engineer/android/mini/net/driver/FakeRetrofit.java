package com.engineer.android.mini.net.driver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * Created on 2021/9/4.
 *
 * @author rookie
 */
public class FakeRetrofit {
    private static volatile FakeRetrofit sInstance = null;

    public static FakeRetrofit getsInstance() {
        if (sInstance == null) {
            synchronized (FakeRetrofit.class) {
                if (sInstance == null) {
                    sInstance = new FakeRetrofit();
                }
            }
        }
        return sInstance;
    }

    private FakeRetrofit() {
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {
        System.out.println("=============create===========");
        TypeVariable<Class<T>>[] array = clazz.getTypeParameters();
        System.out.println(Arrays.toString(array));
        System.out.println(array.length);
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.printf("proxy=%s,method=%s,args=%s\n",
                                proxy.getClass().getName(), method.getName(), Arrays.toString(args));
                        return null;
                    }
                }
        );
    }
}
