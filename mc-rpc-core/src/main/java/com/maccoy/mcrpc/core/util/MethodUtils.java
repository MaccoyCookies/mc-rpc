package com.maccoy.mcrpc.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;

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

//    public static void main(String[] args) {
//        Arrays.stream(MethodUtils.class.getMethods()).forEach(
//                method -> System.out.println(methodSign(method))
//        );
//    }

}
