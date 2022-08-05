package com.jd.bluedragon.utils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.converters.DateConverter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanUtils {

    public static <T> T convert(Object source, Class<T> tClass) {

        if (source != null) {
            try {
                T t= tClass.newInstance();
                ConvertUtils.register(new DateConverter(null), java.util.Date.class);
//                org.apache.commons.beanutils.BeanUtils.copyProperties(t, source);
                org.springframework.beans.BeanUtils.copyProperties(t, source);

                return t;
            } catch (IllegalAccessException e) {
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




}
