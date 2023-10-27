package com.jd.bluedragon.utils;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

import java.lang.reflect.*;
import java.util.*;

public class BeanUtils {

    public static <T> T convert(Object source, Class<T> tClass) {

        if (source != null) {
            try {
                T t= tClass.newInstance();
                org.apache.commons.beanutils.BeanUtils.copyProperties(t, source);

                return t;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> List<T> convert(List sourceList, Class<T> tClass) {

        List<T> list=new ArrayList<T>();

        for (Object source : sourceList) {

            T target=convert(source,tClass);

            list.add(target);
        }

        return list;
    }

    public static <T> List<T> copy(List sourceList, Class<T> tClass) {

        List<T> list=new ArrayList<T>();

        for (Object source : sourceList) {

            T target=copy(source,tClass);

            list.add(target);
        }

        return list;
    }

    public static Map<String,Object> bean2Map(Object orig) {
        Map<String,Object> map = new HashMap<>();
        DynaProperty[] origDescriptors = ((DynaBean)orig).getDynaClass().getDynaProperties();
        DynaProperty[] var4 = origDescriptors;
        int var5;
        int var6;
        String name;
        Object value;
        var5 = origDescriptors.length;
        for(var6 = 0; var6 < var5; ++var6) {
            DynaProperty origDescriptor = var4[var6];
            name = origDescriptor.getName();
                value = ((DynaBean)orig).get(name);
                map.put(name,value);
        }
        return map;
    }


    public static <T> T copy(Object source, Class<T> tClass) {

        try {
            T t = tClass.newInstance();
            Field[] sourceFields = source.getClass().getDeclaredFields();
            for (Field field : sourceFields) {
                field.setAccessible(true);
                String type = containFieldName(t, field.getName());
                if (!"".equals(type) && field.getType().toString().equals(type) && null!=field.get(source)) {
                    org.apache.commons.beanutils.BeanUtils
                            .copyProperty(t, field.getName(), field.get(source));
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String containFieldName(Object obj, String filedName)
            throws Exception {
        String type = "";
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (filedName.equals(f.getName())) {
                type = f.getType().toString();
                break;
            }
        }
        return type;
    }

    public static boolean hasField(Object obj, String filedName) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (filedName.equals(f.getName())) {
                return true;
            }
        }
        return false;
    }


    public static <T> T mapConvertToDto(Map<String, Object> map, Class<T> dtoClass) throws Exception {
        T dto = dtoClass.getDeclaredConstructor().newInstance();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            if (ObjectHelper.isNotNull(fieldName) && ObjectHelper.isNotNull(fieldValue)){
                Field field = dtoClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(dto, fieldValue);
            }
        }

        return dto;
    }

    public static <T> List<T> listMapConvertToDtoList(List<Map<String, Object>> mapList, Class<T> dtoClass) throws Exception {
        List<T> dtoList = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            T dto = mapConvertToDto(map, dtoClass);
            dtoList.add(dto);
        }

        return dtoList;
    }


    public static <T> T mockClassObj(Class<T> clazz) {
        try {
            T object = clazz.getDeclaredConstructor().newInstance();
            setFieldsRecursively(object);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // 使用递归和判断逻辑设置字段值
    private static void setFieldsRecursively(Object object) throws Exception {
        Class<?> type = object.getClass();
        if (isPrimitiveType(type)) {
            return;  // 如果是基础类型，则终止递归
        }
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (Collection.class.isAssignableFrom(field.getType())) {
                Type genericType =field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericType;
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length > 0) {
                        Class<?> typeArgument = (Class<?>) typeArguments[0];
                        field.set(object, Collections.singletonList(mockClassObj(typeArgument)));
                    }
                }
            }
            else if (field.getClass().isArray()) {
                Class<?> componentType = field.getType().getComponentType();
                Object array = java.lang.reflect.Array.newInstance(componentType, 1);
                Object element = mockClassObj(componentType);
                java.lang.reflect.Array.set(array, 0, element);
                field.set(object, array);
            } else if ( !isPrimitiveType(field.getType())) {
                setFieldsRecursively(field);
            } else {
                Class<?> fieldType = field.getType();
                Object value = generateValueForType(fieldType);
                field.set(object, value);
            }
        }
    }

    // 判断是否为基本类型
    private static boolean isPrimitiveType(Class<?> type) {
        return type.isPrimitive() || type == Integer.class || type == Long.class
                || type == Double.class || type == Float.class || type == Boolean.class
                || type == Byte.class || type == Short.class || type == Character.class
                || type == String.class;
    }

    // 根据字段类型生成合适的值
    private static Object generateValueForType(Class<?> fieldType) {
        if (fieldType == Integer.class || fieldType == int.class) {
            return 123;
        } else if (fieldType == Long.class || fieldType == long.class) {
            return 456L;
        } else if (fieldType == String.class) {
            return "ABC";
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return true;
        } else if (fieldType == Double.class || fieldType == double.class) {
            return 3.14;
        } else if (fieldType == Float.class || fieldType == float.class) {
            return 2.718f;
        } else if (fieldType == Byte.class || fieldType == byte.class) {
            return (byte) 1;
        } else if (fieldType == Short.class || fieldType == short.class) {
            return (short) 2;
        } else if (fieldType == Character.class || fieldType == char.class) {
            return 'X';
        } else if (fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            return Array.newInstance(componentType, 0);
        } else if (fieldType.isEnum()) {
            Object[] enumConstants = fieldType.getEnumConstants();
            return enumConstants != null && enumConstants.length > 0 ? enumConstants[0] : null;
        } else {
            try {
                Object instance = fieldType.getDeclaredConstructor().newInstance();
                return instance;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
