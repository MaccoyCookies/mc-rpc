package com.maccoy.mcrpc.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.valueOf(origin.toString());
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
        if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return Byte.valueOf(origin.toString());
        }
        if (type.equals(Short.class) || type.equals(Short.TYPE)) {
            return Short.valueOf(origin.toString());
        }
        if (type.equals(Character.class) || type.equals(Character.TYPE)) {
            return origin.toString().charAt(0);
        }
        if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
            return Boolean.valueOf(origin.toString());
        }
        return null;
    }

    public static Object castMethodResult(Method method, Object data) {
        Class<?> type = method.getReturnType();
        if (data instanceof JSONObject jsonObject) {
            if (Map.class.isAssignableFrom(type)) {
                Map resultMap = new HashMap();
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                    jsonObject.entrySet().stream().forEach(obj -> {
                        Object key = TypeUtils.cast(obj.getKey(), keyType);
                        Object value = TypeUtils.cast(obj.getKey(), valueType);
                        resultMap.put(key, value);
                    });
                }
                return resultMap;
            } else {
                return jsonObject.toJavaObject(type);
            }
        } else if (data instanceof JSONArray jsonArray) {
            Object[] arr = jsonArray.toArray();
            if (type.isArray()) {
                Class<?> componentType = type.getComponentType();
                Object resArray = Array.newInstance(componentType, arr.length);
                for (int i = 0; i < arr.length; i++) {
                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                        Array.set(resArray, i, arr[i]);
                    } else {
                        Object castObject = TypeUtils.cast(arr[i], componentType);
                        Array.set(resArray, i, castObject);
                    }
                }
                return resArray;
            } else if (List.class.isAssignableFrom(type)) {
                List<Object> resList = new ArrayList<>(arr.length);
                // 范型？
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Class<?> actualType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    for (Object obj : arr) {
                        resList.add(TypeUtils.cast(obj, actualType));
                    }
                } else {
                    resList.addAll(Arrays.asList(arr));
                }
                return resList;
            } else {
                return null;
            }
        } else {
            return TypeUtils.cast(data, type);
        }
    }

}
