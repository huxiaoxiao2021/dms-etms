package com.jd.bluedragon.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 枚举工具类.
 * <p> 用于带有一个 Integer 和 String 的枚举类
 * <p> 可根据值获取名称, 或根据名称获取值
 */
@Slf4j
public final class ValueNameEnumUtils {

    private ValueNameEnumUtils() {
    }

    public static final String DEFAULT_VALUE_NAME = "value";
    public static final String DEFAULT_NAME_NAME = "name";

    /**
     * 判断给定值是否在枚举类选项的值.
     * <p> 如无整型字段, 抛出异常
     * <p> 如有多个整型字段, 使用第一个
     */
    public static <E extends Enum<E>> boolean valueOfEnum(final Class<E> enumClass, Integer value) {
        String fieldName = getFirstIntegerField(enumClass).getName();
        String methodName = getGetterName(fieldName);

        try {
            Method getter = enumClass.getMethod(methodName);
            for (E e : Arrays.asList(enumClass.getEnumConstants())) {
                if (getter.invoke(e).equals(value)) {
                    return true;
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("枚举类字段无取值方法: " + enumClass.getSimpleName() + "." + fieldName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("枚举类字段取值方法无法访问: " + enumClass.getSimpleName());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("枚举类字段取值方法调用失败: " + enumClass.getSimpleName());
        }

        return false;
    }

    /**
     * 返回给定整型值的对应的枚举类.
     * <p> 如无整型字段, 抛出异常
     * <p> 如值对应多个枚举, 使用第一个
     */
    public static <E extends Enum<E>> E getByValue(final Class<E> enumClass, Integer value) {
        String fieldName = getFirstIntegerField(enumClass).getName();
        String methodName = getGetterName(fieldName);

        try {
            Method getter = enumClass.getMethod(methodName);
            for (E e : Arrays.asList(enumClass.getEnumConstants())) {
                if (getter.invoke(e).equals(value)) {
                    return e;
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("枚举类字段无取值方法: " + enumClass.getSimpleName() + "." + fieldName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("枚举类字段取值方法无法访问: " + enumClass.getSimpleName());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("枚举类字段取值方法调用失败: " + enumClass.getSimpleName());
        }

        return null;
    }

    /**
     * 返回给字符串的对应的枚举类.
     * <p> 如无字符串字段, 抛出异常
     * <p> 如字符串对应多个枚举, 使用第一个
     */
    public static <E extends Enum<E>> E getByName(final Class<E> enumClass, String name) {
        String fieldName = getFirstStringField(enumClass).getName();
        String methodName = getGetterName(fieldName);

        try {
            Method getter = enumClass.getMethod(methodName);
            for (E e : Arrays.asList(enumClass.getEnumConstants())) {
                if (name != null && name.equals((String) getter.invoke(e))) {
                    return e;
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("枚举类字段无取值方法: " + enumClass.getSimpleName() + "." + fieldName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("枚举类字段取值方法无法访问: " + enumClass.getSimpleName());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("枚举类字段取值方法调用失败: " + enumClass.getSimpleName());
        }

        return null;
    }

    /**
     * 返回给定整型值的对应的枚举类的字符串值
     * <p> 如无整型字段, 抛出异常
     * <p> 如值对应多个枚举, 使用第一个
     */
    public static <E extends Enum<E>> String getNameByValue(final Class<E> enumClass, Integer value) {
        E e = getByValue(enumClass, value);
        if (e == null) {
            log.warn("Enum of the value is not exist");
            return null;
        }
        String fieldName = getFirstStringField(enumClass).getName();
        String methodName = getGetterName(fieldName);
        try {
            Method getter = enumClass.getMethod(methodName);
            return (String) getter.invoke(e);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("枚举类字段无取值方法: " + enumClass.getSimpleName() + "." + fieldName);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("枚举类字段取值方法无法访问: " + enumClass.getSimpleName());
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("枚举类字段取值方法调用失败: " + enumClass.getSimpleName());
        }
    }

    /**
     * 返回给定枚举类, 指定字段值名称映射
     * <p> 如字段名或类型错误, 则抛出异常
     */
    public static <E extends Enum<E>> Map<Integer, String> getValueNameMap(@NonNull final Class<E> enumClass, String valueFieldName, String nameFieldName) {
        if (StringUtils.isEmpty(valueFieldName) || StringUtils.isEmpty(nameFieldName)) {
            log.warn("Value field name or name field name is blank");
            return Collections.EMPTY_MAP;
        }

        Map<Integer, String> result = new HashMap<>();
        try {
            Method valueGetter = enumClass.getMethod(getGetterName(valueFieldName));
            Method nameGetter = enumClass.getMethod(getGetterName(nameFieldName));
            for (E e : EnumUtils.getEnumList(enumClass)) {
                try {
                    result.put((Integer) valueGetter.invoke(e), (String) nameGetter.invoke(e));
                } catch (IllegalAccessException | InvocationTargetException | ClassCastException e1) {
                    log.error("Enum class getter invoke failed: {}", enumClass.getSimpleName());
                    throw new RuntimeException("Enum class getter invoke failed: " + enumClass.getSimpleName());
                }
            }
            return result;
        } catch (NoSuchMethodException e) {
            log.error("Enum class getter method is not exist: {}", enumClass.getSimpleName());
            throw new RuntimeException("Enum class getter method is not exist: " + enumClass.getSimpleName());
        }
    }

    public static <E extends Enum<E>> Map<Integer, String> getValueNameMap(@NonNull final Class<E> enumClass, String nameFieldName) {
        return getValueNameMap(enumClass, DEFAULT_VALUE_NAME, nameFieldName);
    }

    public static <E extends Enum<E>> Map<Integer, String> getValueNameMap(@NonNull final Class<E> enumClass) {
        return getValueNameMap(enumClass, DEFAULT_VALUE_NAME, DEFAULT_NAME_NAME);
    }

    public static <E extends Enum<E>> Map<Integer, E> getValueMap(@NonNull final Class<E> enumClass, String valueFieldName) {
        if (StringUtils.isEmpty(valueFieldName)) {
            log.warn("Value field name is blank");
            return Collections.EMPTY_MAP;
        }

        Map<Integer, E> result = new HashMap<>();
        try {
            Method valueGetter = enumClass.getMethod(getGetterName(valueFieldName));
            for (E e : EnumUtils.getEnumList(enumClass)) {
                try {
                    result.put((Integer) valueGetter.invoke(e), e);
                } catch (IllegalAccessException | InvocationTargetException | ClassCastException e1) {
                    log.error("Enum class getter invoke failed: {}", enumClass.getSimpleName());
                    throw new RuntimeException("Enum class getter invoke failed: " + enumClass.getSimpleName());
                }
            }
            return result;
        } catch (NoSuchMethodException e) {
            log.error("Enum class getter method is not exist: {}", enumClass.getSimpleName());
            throw new RuntimeException("Enum class getter method is not exist: " + enumClass.getSimpleName());
        }
    }

    public static <E extends Enum<E>> Map<Integer, E> getValueMap(@NonNull final Class<E> enumClass) {
        return getValueMap(enumClass, DEFAULT_VALUE_NAME);
    }


    private static <E extends Enum<E>> Field getFirstIntegerField(final Class<E> enumClass) {
        for (Field field : enumClass.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(Integer.class)) {
                return field;
            }
        }
        throw new IllegalStateException("枚举类无数值字段: " + enumClass.getSimpleName());
    }

    private static <E extends Enum<E>> Field getFirstStringField(final Class<E> enumClass) {
        for (Field field : enumClass.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(String.class)) {
                return field;
            }
        }
        throw new IllegalStateException("枚举类无字符串字段: " + enumClass.getSimpleName());
    }

    private static String getGetterName(String fieldName) {
        return StringUtils.isEmpty(fieldName) ? "" : "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
