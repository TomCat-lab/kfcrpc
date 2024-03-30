package com.cola.kfcrpc.core.utils;

import java.lang.reflect.Method;

/**
 * Class: MethodUtils
 * Author: cola
 * Date: 2024/3/30
 * Description: method utils
 */

public class MethodUtils {
    public static boolean checkObejectMethod(Method method){
        return method.getDeclaringClass().equals(Object.class);
    }

    /**
     * Author: cola
     * Date: 2024/3/30
     * Description: build method sign
     */

    public static String sign(Method method) {
        String name = method.getName();
        StringBuilder sb = new StringBuilder(name);
        sb.append("@").append(method.getParameterCount());
        for (Class<?> parameterType : method.getParameterTypes()) {
            sb.append("_").append(parameterType.getCanonicalName());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        for (Method method : MethodUtils.class.getMethods()) {
            System.out.printf( sign(method));
            System.out.printf("\n");
        }

    }
}
