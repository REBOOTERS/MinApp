package com.engineer.android.mini.net.driver;

import androidx.annotation.NonNull;

import com.engineer.android.mini.net.WeChatCountList;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created on 2021/9/4.
 *
 * @author rookie
 */
public class FakeRetrofit {
    private static volatile FakeRetrofit instance = null;

    public static FakeRetrofit getInstance() {
        if (instance == null) {
            synchronized (FakeRetrofit.class) {
                if (instance == null) {
                    instance = new FakeRetrofit();
                }
            }
        }
        return instance;
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
