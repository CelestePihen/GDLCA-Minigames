package fr.cel.gameapi.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) throw e;
            else return getField(superClass, fieldName);
        }
    }

    public static Object getField(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            result = field.get(instance);
            field.setAccessible(false);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return result;
    }

    public static void invoke(Method method, Object obj, Object... args) {
        try {
            method.setAccessible(true);
            method.invoke(obj, args);
            method.setAccessible(false);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
