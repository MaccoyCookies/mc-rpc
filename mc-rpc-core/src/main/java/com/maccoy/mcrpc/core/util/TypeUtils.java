package com.maccoy.mcrpc.core.util;

public class TypeUtils {

    public static Object cast(Object origin, Class<?> type) {
        if (origin == null) return null;
        Class<?> aClass = origin.getClass();
        if (type.isAssignableFrom(aClass)) return origin;

        if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.valueOf(origin.toString());
        }
        return null;
    }

}
