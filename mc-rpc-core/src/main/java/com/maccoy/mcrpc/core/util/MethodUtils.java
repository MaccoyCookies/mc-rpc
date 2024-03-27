package com.maccoy.mcrpc.core.util;

import com.maccoy.mcrpc.core.annotation.McConsumer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodUtils {

    public static boolean checkLocalMethod(final String methodName) {
        return "toString".equals(methodName)
                || "hashCode".equals(methodName)
                || "notifyAll".equals(methodName)
                || "equals".equals(methodName)
                || "wait".equals(methodName)
                || "getClass".equals(methodName)
                || "notify".equals(methodName)
                || "finalize".equals(methodName)
                || "clone".equals(methodName);
    }

    public static boolean checkLocalMethod(final Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    public static String methodSign(Method method) {
        StringBuilder stringBuilder = new StringBuilder(method.getName());
        stringBuilder.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(
                parameterType -> stringBuilder.append("_").append(parameterType.getCanonicalName())
        );
        return stringBuilder.toString();
    }

    public static List<Field> findAnnotatedField(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        List<Field> res = new ArrayList<>();
        while (aClass.getSuperclass() != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    res.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return res;
    }
}
