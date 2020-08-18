package com.jd.bluedragon.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.BeanUtils;

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
    /**
     * 对象字段列表缓存:类声明的所有字段(不含父类)
     */
    private static Cache<Class<?>, List<Field>> classDeclaredFieldsListCache
            = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1024)
            .concurrencyLevel(16)
            .initialCapacity(256)
            .build();
    /**
     * 对象字段列表缓存:类声明的所有字段(含父类)
     */
    private static Cache<Class<?>, List<Field>> classAllFieldsListCache
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
    /**
     * 获取class对象的所有属性(不含父类)
     * @param classType
     * @return 以字段名为key的map
     */
    public static List<Field> getDeclaredFieldsList(Class<?> classType){
    	List<Field> fields = classDeclaredFieldsListCache.getIfPresent(classType);
    	/**
    	 * 检查缓存是否存在，不存在时解析并放入缓存
    	 */
		if (fields==null) {
			synchronized (classDeclaredFieldsListCache) {
				fields = classDeclaredFieldsListCache.getIfPresent(classType);
				if (fields==null){
					fields = new ArrayList<Field>();
					Field[] fieldsArray = classType.getDeclaredFields();
					if(fieldsArray!=null){
						for(Field f:fieldsArray){
							fields.add(f);
						}
					}
					classDeclaredFieldsListCache.put(classType,fields);
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
    public static List<Field> getAllFieldsList(Class<?> classType){
    	List<Field> fields = classAllFieldsListCache.getIfPresent(classType);
    	/**
    	 * 检查缓存是否存在，不存在时解析并放入缓存
    	 */
		if (fields==null) {
			synchronized (classAllFieldsListCache) {
				fields = classAllFieldsListCache.getIfPresent(classType);
				if (fields==null){
					fields = new ArrayList<Field>();
					Class<?> superClass = classType.getSuperclass();
					//递归获取父类的所有属性
					if(superClass!=null&&!superClass.equals(Object.class)){
						fields.addAll(getAllFieldsList(superClass));
					}
					fields.addAll(getDeclaredFieldsList(classType));
					classAllFieldsListCache.put(classType, fields);
				}
			}
		}
    	return fields;
    }    
    /**
     * 比较2个对象
     * <p>1、同时为空返回0
     * <p>2、都不为空，返回o1.compareTo(o2)比较结果
     * <p>3、o1为空o2不为空，返回-1
     * <p>4、o2为空哦o1不为空，返回1
     * @param date
     * @param date1
     * @return
     */
    public static <T> int compare(T o1,T o2) {
    	if(o1 != null && o2 != null){
    		if(o1 instanceof Comparable){
    			return ((Comparable)o1).compareTo(((Comparable)o2));
    		}else{
    			throw new RuntimeException("The params must be comparable!");
    		}
    	}else if(o1 == null && o2 == null){
    		return 0;
    	}else if(o1 == null && o2 != null){
    		return -1;
    	}else{
    		return 1;
    	}
    }
    /**
     * 根据属性名获取类的属性对象
     * @param classType 类
     * @param fieldName 属性名
     * @return
     */
	public static Field getField(Class<?> classType,String fieldName){
		return getAllFields(classType).get(fieldName);
    }
    /**
     * 根据属性名获取实例对象类-属性对象
     * @param t 类-实例
     * @param fieldName 属性名
     * @return
     */
	public static <T> Field getField(T t,String fieldName){
		return getField(t.getClass(),fieldName);
    }
    /**
     * 给指定的对象-字段 赋值
     * @param obj 指定的对象
     * @param field 指定的属性
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException
     */
    public static <T,V> void setValue(T obj,Field field,V fieldVal) throws IllegalArgumentException, IllegalAccessException{
    	if(obj!=null&&field!=null&&fieldVal!=null){
    		field.setAccessible(true);
    		field.set(obj, fieldVal);
    	}
    }

    public static <T,V> void setValue(T obj,String fieldName, V fieldVal)throws IllegalArgumentException, IllegalAccessException{
		setValue(obj,getField(obj,fieldName),fieldVal);
	}
    /**
     * 获取指定对象的属性值
     * @param obj 对象
     * @param field 属性
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
	public static <T,V> V getValue(T obj,Field field) throws IllegalArgumentException, IllegalAccessException{
    	V res = null;
    	if(obj!=null&&field!=null){
    		field.setAccessible(true);
    		res = (V) field.get(obj);
    	}
    	return res;
    }
    /**
     * 获取指定对象的属性值，对象属性值为空，返回默认值
     * @param obj 对象
     * @param field 属性
     * @param defaultVal 对象属性值为空时，返回的默认值
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
	public static <T,V> V getValue(T obj,Field field,V defaultVal) throws IllegalArgumentException, IllegalAccessException{
    	V res = getValue(obj,field);
    	if(res!=null){
    		return res;
    	}
    	return defaultVal;
    }
    /**
     * 获取指定对象的属性值，对象属性值为空，返回默认值
     * @param obj 对象
     * @param fieldName 属性名称
     * @param defaultVal 对象属性值为空时，返回的默认值
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
	public static <T,V> V getValue(T obj,String fieldName) throws IllegalArgumentException, IllegalAccessException{
		if(obj!=null&&StringHelper.isNotEmpty(fieldName)){
			return getValue(obj,getField(obj,fieldName));
		}
		return null;
    }
    /**
     * 获取指定对象的属性值，对象属性值为空，返回默认值
     * @param obj 对象
     * @param fieldName 属性名称
     * @param defaultVal 对象属性值为空时，返回的默认值
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
	public static <T,V> V getValue(T obj,String fieldName,V defaultVal) throws IllegalArgumentException, IllegalAccessException{
		if(obj!=null&&StringHelper.isNotEmpty(fieldName)){
			return getValue(obj,getField(obj,fieldName),defaultVal);
		}
		return defaultVal;
    }

	/**
	 * 对象复制
	 *
	 * @param source
	 * @return
	 */
	public static Object copyBeans(Object source) {
		Object target = BeanUtils.instantiate(source.getClass());
		BeanUtils.copyProperties(source, target);
		return target;
	}
}