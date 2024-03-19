package com.maccoy.mcrpc.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

public class TypeUtils {

    public static Object cast(Object origin, Class<?> type) {
        if (origin == null) return null;
        Class<?> aClass = origin.getClass();
        if (type.isAssignableFrom(aClass)) return origin;

        if (origin instanceof HashMap map) {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }
        if (origin instanceof List list) {
            origin = list.toArray();
            if (type.isArray()) {
                int length = Array.getLength(origin);
                Class<?> componentType = type.getComponentType();
                Object resArray = Array.newInstance(componentType, length);
                for (int i = 0; i < length; i++) {
                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                        Array.set(resArray, i, Array.get(origin, i));
                    } else {
                        Object castObject = cast(Array.get(origin, i), componentType);
                        Array.set(resArray, i, castObject);
                    }
                }
                return resArray;
            }
        }
        if (origin instanceof JSONObject jsonObject) {
            return jsonObject.toJavaObject(type);
        }
        if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.valueOf(origin.toString());
        }
        if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Float.valueOf(origin.toString());
        }
        if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return Double.valueOf(origin.toString());
        }
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.valueOf(origin.toString());
        }
        return null;
    }

}
