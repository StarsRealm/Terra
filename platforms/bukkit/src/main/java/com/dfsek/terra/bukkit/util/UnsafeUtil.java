package com.dfsek.terra.bukkit.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;


public class UnsafeUtil {
    static final Unsafe unsafe;

    static {
        Field unsafeField = null;
        try {
            unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void putFinal(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            long fieldOffset = unsafe.objectFieldOffset(field);
            unsafe.putObject(target, fieldOffset, value);
        } catch(NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
