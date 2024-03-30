package com.cola.kfcrpc.core.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class: TypeUtils
 * Author: cola
 * Date: 2024/3/30
 * Description: 类型转换工具类
 */

public class TypeUtils {

    public static Object convert(Object data, Method method, Class<?> paramType) throws Exception {
        // 获取方法的返回类型
        if (data == null) return null;
        Class<?> returnType = null;
        if (paramType == null){
            returnType = method.getReturnType();
        }else {
            returnType = paramType;
        }

        if(returnType.isArray()) {
            if(data instanceof List list) {
                data = list.toArray();
            }
            int length = Array.getLength(data);
            Class<?> componentType = returnType.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                    Array.set(resultArray, i, Array.get(data, i));
                } else {
                    Object castObject = convert(Array.get(data, i), componentType);
                    Array.set(resultArray, i, castObject);
                }
            }
            return resultArray;
        }

        // 如果data已经是正确的类型，直接返回
//        if (returnType.isAssignableFrom(data.getClass())) {
//            return data;
//        }

        return convert(data,returnType);

    }

    public static Object convert(Object data, Class<?> returnType,Type genericType) throws Exception {
        if (List.class.isAssignableFrom(returnType)) {
            List<?> dataList = (List<?>) data;
            List<Object> convertedList = new ArrayList<>();
            for (Object element : dataList) {
                if (genericType instanceof  ParameterizedType g) {
                    String typeName = g.getActualTypeArguments()[0].getTypeName();
                    Object convertedElement = convert(element,Class.forName(typeName));
                    convertedList.add(convertedElement);
                }
                // 转换集合中的每个元素到指定的泛型类型
            }
            return convertedList;
        }
        return convert(data,returnType);
    }

    public static Object convert(Object data, Class<?> returnType) throws Exception{
        if (data == null) return null;

        if (returnType.isInstance(data)) return data;

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


        // 如果不能转换，抛出异常或返回null
        throw new IllegalArgumentException("Unsupported return type: " + returnType.getName());
    }



}

