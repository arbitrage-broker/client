package com.eshop.client.util;

import java.lang.reflect.Method;

public class ReflectionUtil {
    public static Object invokeMethod(String className, String methodName) {
        try {
            Class clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName);
            return method.invoke(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object invokeMethod(String className, String methodName, Class<?>[] parameterTypes, Object... parameters) {
        try {
            Class clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, parameterTypes);
            return method.invoke(clazz, parameters);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }
}
