package com.cola.kfcrpc.core.utils;

import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class: TypeUtils
 * Author: cola
 * Date: 2024/3/30
 * Description: 类型转换工具类
 */

public class TypeUtils {

    public static Object convert(Object data, Method method) throws Exception {
        // 获取方法的返回类型
        Class<?> returnType = method.getReturnType();
       return convert(data,returnType);

    }



    public static Object convert(Object data, Class<?> returnType) throws Exception{
        // 如果data已经是正确的类型，直接返回
        if (returnType.isInstance(data)) {
            return data;
        }

        // 基于返回类型进行转换
        if (returnType == Integer.TYPE || returnType == Integer.class) {
            return Integer.valueOf(data.toString());
        } else if (returnType == Double.TYPE || returnType == Double.class) {
            return Double.valueOf(data.toString());
        }else if (returnType == Long.TYPE || returnType == Long.class) {
            return Long.valueOf(data.toString());
        }  else if (returnType == Float.TYPE || returnType == Float.class) {
            return Float.valueOf(data.toString());
        }else if (returnType == Boolean.TYPE || returnType == Boolean.class) {
            return Boolean.valueOf(data.toString());
        } else if (returnType == String.class) {
            return String.valueOf(data);
        }else if (returnType == Character.TYPE ||returnType == Character.class ) {
            return data.toString().charAt(0);
        }

        // 可以根据需要添加更多类型的转换
        //LinkedHashMap
        if (data instanceof LinkedHashMap<?,?> result ){
            Object instance = returnType.newInstance();
            for (Field field : instance.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = result.get(field.getName());
                field.set(instance,value);
                field.setAccessible(false);
            }
            return instance;
        }

        if (data instanceof JSONArray result){
            return result.toJavaObject(returnType);
        }

        if (data instanceof List result){
            Object instance = returnType.newInstance();
            Field[] fields = instance.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Object value = convert(result.get(i), returnType.getComponentType());
                fields[i].set(instance,value);
                fields[i].setAccessible(false);
            }
            return instance;
        }

        // 如果不能转换，抛出异常或返回null
        throw new IllegalArgumentException("Unsupported return type: " + returnType.getName());
    }



}

