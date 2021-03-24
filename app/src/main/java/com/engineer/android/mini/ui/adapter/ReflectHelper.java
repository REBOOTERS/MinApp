package com.engineer.android.mini.ui.adapter;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReflectHelper {


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> void getList(Object object, T dateClass) {
        List<T> resultList = new ArrayList<>();
        if (!Objects.isNull(object)) {

            Field[] fields = getAllFields(object.getClass());
            Field[] filterList = filterField(fields);
            Arrays.stream(filterList).forEach(var -> {
                //List集合
                if (List.class.isAssignableFrom(var.getType())) {
                    Type type = var.getGenericType();
                    if (type instanceof ParameterizedType) {
                        if (!var.isAccessible()) {
                            var.setAccessible(true);
                        }
                        //获取到属性值的字节码
                        try {
                            Class<?> clzz = var.get(object).getClass();
                            //反射调用获取到list的size方法来获取到集合的大小
                            Method sizeMethod = clzz.getDeclaredMethod("size");
                            if (!sizeMethod.isAccessible()) {
                                sizeMethod.setAccessible(true);
                            }
                            //集合长度
                            int size = (int) sizeMethod.invoke(var.get(object));
                            //循环遍历获取到数据
                            for (int i = 0; i < size; i++) {
                                //反射获取到list的get方法
                                Method getMethod = clzz.getDeclaredMethod("get", int.class);
                                //调用get方法获取数据
                                if (!getMethod.isAccessible()) {
                                    getMethod.setAccessible(true);
                                }
                                T var1 = (T) getMethod.invoke(var.get(object), i);
                                resultList.add(var1);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Log.e("reflect", "resultList:" + resultList);
            resultList.stream().forEach(var -> {
                System.out.println("反射获取到的数据是什么:" + var);
                Log.e("reflect", "反射获取到的数据是什么:" + var);
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Field[] filterField(Field[] fields) {
        List<Field> tempList = Arrays.stream(fields).filter(field -> null != field
                && !Modifier.isFinal(field.getModifiers())
                && !Modifier.isStatic(field.getModifiers())
                && !Modifier.isAbstract(field.getModifiers())).collect(Collectors.toList());


        int arrLength = tempList.isEmpty() ? 1 : tempList.size();

        Field[] resultArr = new Field[arrLength];
        if (!tempList.isEmpty()) {
            tempList.toArray(resultArr);
        }

        return resultArr;
    }


    public static Field[] getAllFields(Class c) {
        List<Field> fieldList = new ArrayList<>();
        while (c != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(c.getDeclaredFields())));
            c = c.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

}
