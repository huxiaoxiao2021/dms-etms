package com.jd.bluedragon.utils;


import org.springframework.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;

public class BeanCopyUtil {
    /**
     * BeanCopier缓存
     */
    private static ConcurrentHashMap<String, BeanCopier> copierCache = new ConcurrentHashMap<>();

    /**
     * 拷贝对象
     *
     * @param source 源文件的
     * @param target 目标文件
     */
    public static void copy(Object source, Object target) {
        String key = genKey(source.getClass(), target.getClass());
        BeanCopier beanCopier = copierCache.get(key);
        if (beanCopier == null) {
            beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            copierCache.put(key, beanCopier);
        }
        beanCopier.copy(source, target, null);
    }

    /**
     * 生成key
     *
     * @param srcClazz    源文件的class
     * @param targetClazz 目标文件的class
     * @return string
     */
    private static String genKey(Class<?> srcClazz, Class<?> targetClazz) {
        return String.format("%s_%s", srcClazz.getName(), targetClazz.getName());
    }
}