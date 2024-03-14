package com.maccoy.mcrpc.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodUtils {

    public static boolean checkLocalMethod(final String method) {
        return "toString".equals(method)
                || "hashCode".equals(method)
                || "notifyAll".equals(method)
                || "equals".equals(method)
                || "wait".equals(method)
                || "getClass".equals(method)
                || "notify".equals(method);
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

//    public static void main(String[] args) {
//        Arrays.stream(MethodUtils.class.getMethods()).forEach(
//                method -> System.out.println(methodSign(method))
//        );
//    }

}
