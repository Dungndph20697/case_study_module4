package com.codegym.case_study_module4.controller.user;

import java.lang.reflect.Field;

public class TestUtils {
    public static void injectField(Object target, String fieldName, Object value) {
        Class<?> clazz = target.getClass();
        while (clazz != null) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                f.set(target, value);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to set field '" + fieldName + "' on target", e);
            }
        }
        throw new RuntimeException("Field '" + fieldName + "' not found on target " + target.getClass());
    }
}

