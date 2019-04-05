package com.kaity.dev.cuongproject.adapter;

import java.lang.reflect.Method;

public class ReflectHelper {
    public static void invokeMethodIfExists(String methodName, Object target, Class<?>[] parameterTypes, Object[] parameters) {
        Class c = target.getClass();
        try {
            Method method = c.getMethod(methodName, parameterTypes);
            method.invoke(target, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
