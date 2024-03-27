package com.maccoy.mcrpc.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.catalina.User;

import java.lang.reflect.Field;

/**
 * @author Maccoy
 * @date 2024/3/24 21:00
 * Description
 */
public class MockUtils {

    public static Object mock(Class<?> clazz) {

        if (Number.class.isAssignableFrom(clazz)) {
            return 19;
        }
        if (clazz.equals(String.class)) {
            return "Maccoy";
        }
        if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE)) {
            return 19;
        }
        if (clazz.equals(Long.class) || clazz.equals(Long.TYPE)) {
            return 19L;
        }
        if (clazz.equals(Float.class) || clazz.equals(Float.TYPE)) {
            return 19.0;
        }
        if (clazz.equals(Double.class) || clazz.equals(Double.TYPE)) {
            return 19.0;
        }
        if (clazz.equals(Byte.class) || clazz.equals(Byte.TYPE)) {
            return 19;
        }
        if (clazz.equals(Short.class) || clazz.equals(Short.TYPE)) {
            return 19;
        }
        if (clazz.equals(Character.class) || clazz.equals(Character.TYPE)) {
            return "M";
        }
        if (clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE)) {
            return true;
        }
        return mockPojo(clazz);
    }

    @SneakyThrows
    private static Object mockPojo(Class<?> clazz) {
        Object res = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            field.set(res, mock(type));
        }
        return res;
    }

    public static void main(String[] args) {
        System.out.println(mock(UserDto.class));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor

    public static class UserDto {

        private Integer id;

        private String name;
    }
}
