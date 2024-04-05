package com.cola.kfcrpc.core.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.*;
import java.util.*;

/**
 * Class: TypeUtils
 * Author: cola
 * Date: 2024/3/30
 * Description: 类型转换工具类
 */

public class TypeUtils {

    public static Object convert(Object data, Method method, Class<?> paramType) throws RuntimeException {
        // 获取方法的返回类型
        if (data == null) return null;
        Class<?> returnType = null;
        if (paramType == null){
            returnType = method.getReturnType();
        }else {
            returnType = paramType;
        }



        return convert(data,returnType,method.getGenericReturnType());

    }

    public static Object convert(Object data, Class<?> returnType,Type genericType) throws RuntimeException {

        if (List.class.isAssignableFrom(returnType)) {
            List<?> dataList = (List<?>) data;
            List<Object> convertedList = new ArrayList<>();
            for (Object element : dataList) {
                if (genericType instanceof  ParameterizedType g) {
                    String typeName = g.getActualTypeArguments()[0].getTypeName();
                    if (typeName.contains("<")){
                        typeName = typeName.substring(0, typeName.indexOf("<"));
                    }
                    Object convertedElement = null;
                    try {
                        convertedElement = convert(element,Class.forName(typeName),g.getActualTypeArguments()[0]);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    convertedList.add(convertedElement);
                }
                // 转换集合中的每个元素到指定的泛型类型
            }
            return convertedList;
        }

        if (data instanceof  JSONObject result){
            if (Map.class.isAssignableFrom(returnType)){
                HashMap<Object,Object> map = new HashMap<>();
                if (genericType instanceof ParameterizedType type){
                    Class<?> k = (Class<?>) type.getActualTypeArguments()[0];
                    Class<?> v = (Class<?>) type.getActualTypeArguments()[1];

                    result.entrySet().stream().forEach(r->{
                        String key = r.getKey();
                        Object value = r.getValue();
                        try {
                            Object convertK = convert(key, k);
                            Object convertV = convert(value, v);
                            map.put(convertK,convertV);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    });
                }
                return map;
            }

        }

        if (returnType.isArray()){
            if (data instanceof List list){
                data =  list.toArray();
            }

            int length = Array.getLength(data);
            Class<?> componentType = returnType.getComponentType();
            Object instance = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")){
                    Array.set(instance,i,Array.get(data,i));
                }else {
                    Object converted = convert(Array.get(data, i), componentType);
                    Array.set(instance,i,converted);
                }
            }

            return instance;

        }
        return convert(data,returnType);
    }

    public static Object convert(Object data, Class<?> returnType) throws RuntimeException{
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
            Object instance = null;
            try {
                instance = returnType.newInstance();
                for (Field field : instance.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = result.get(field.getName());
                    field.set(instance,value);
                    field.setAccessible(false);
                }
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return instance;
        }

        if (data instanceof JSONArray result){
            return result.toJavaObject(returnType);
        }

        if (data instanceof JSONObject result){
            return result.toJavaObject(returnType);
        }

        // 如果不能转换，抛出异常或返回null
        throw new IllegalArgumentException("Unsupported return type: " + returnType.getName());
    }



}

