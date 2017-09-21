package com.jd.bluedragon.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ObjectHelper {
    /**
     * 对象字段缓存 key:类 val:类声明的所有字段(不含父类)
     */
    private static Cache<Class<?>, Map<String,Field>> classDeclaredFieldsCache
            = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1024)
            .concurrencyLevel(16)
            .initialCapacity(256)
            .build();
    /**
     * 对象字段缓存 key:类 val:类所有字段(含父类)
     */
    private static Cache<Class<?>, Map<String,Field>> classAllFieldsCache
            = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1024)
            .concurrencyLevel(16)
            .initialCapacity(256)
            .build();
    public static Boolean isEmpty(Boolean object) {
        if (object == null || !object) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    public static Boolean isEmpty(Object object) {
        if (object == null) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
    
    public static Boolean isNotEmpty(Object object) {
        if (object == null) {
            return Boolean.FALSE;
        }
        
        return Boolean.TRUE;
    }
    /**
     * 获取class对象的所有属性(不含父类)
     * @param classType
     * @return 以字段名为key的map
     */
    public static Map<String,Field> getDeclaredFields(Class<?> classType){
    	Map<String,Field> fields = classDeclaredFieldsCache.getIfPresent(classType);
    	/**
    	 * 检查缓存是否存在，不存在时解析并放入缓存
    	 */
		if (fields==null) {
			synchronized (classDeclaredFieldsCache) {
				fields = classDeclaredFieldsCache.getIfPresent(classType);
				if (fields==null){
					fields = new HashMap<String,Field>();
					Field[] fieldsArray = classType.getDeclaredFields();
					if(fieldsArray!=null){
						for(Field f:fieldsArray){
							fields.put(f.getName(), f);
						}
					}
					classDeclaredFieldsCache.put(classType, fields);
				}
			}
		}
    	return fields;
    }
    /**
     * 获取class对象的所有属性(含父类)
     * @param classType
     * @return 以字段名为key的map
     */
    public static Map<String,Field> getAllFields(Class<?> classType){
    	Map<String,Field> fields = classAllFieldsCache.getIfPresent(classType);
    	/**
    	 * 检查缓存是否存在，不存在时解析并放入缓存
    	 */
		if (fields==null) {
			synchronized (classAllFieldsCache) {
				fields = classAllFieldsCache.getIfPresent(classType);
				if (fields==null){
					fields = new HashMap<String,Field>();
					Class<?> superClass = classType.getSuperclass();
					//递归获取父类的所有属性
					if(superClass!=null&&!superClass.equals(Object.class)){
						fields.putAll(getAllFields(superClass));
					}
					fields.putAll(getDeclaredFields(classType));
					classAllFieldsCache.put(classType, fields);
				}
			}
		}
    	return fields;
    }
}