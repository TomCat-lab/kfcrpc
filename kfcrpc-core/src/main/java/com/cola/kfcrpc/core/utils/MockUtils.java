package com.cola.kfcrpc.core.utils;

public class MockUtils {
    public static Object mock(Class<?> returnType){
        if (returnType == Integer.TYPE || returnType == Integer.class) {
            return 1;
        } else if (returnType == Double.TYPE || returnType == Double.class) {
            return 1.1;
        }else if (returnType == Long.TYPE || returnType == Long.class) {
            return 1L;
        }  else if (returnType == Float.TYPE || returnType == Float.class) {
            return 1.1f;
        }else if (returnType == Boolean.TYPE || returnType == Boolean.class) {
            return true;
        } else if (returnType == String.class) {
            return "this is mock string";
        }else if (returnType == Character.TYPE ||returnType == Character.class ) {
            return "s";
        }

        return null;
    }
}
