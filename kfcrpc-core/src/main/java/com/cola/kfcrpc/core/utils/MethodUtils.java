package com.cola.kfcrpc.core.utils;

import java.lang.reflect.Method;

public class MethodUtils {
    public static boolean checkObejectMethod(Method method){
        return method.getDeclaringClass().equals(Object.class);
    }
}
