package com.jd.bluedragon.utils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-21 下午09:25:34
 *
 * 类说明
 */
public class ObjectMapHelper {
	
	private final static Logger log = LoggerFactory.getLogger(ObjectMapHelper.class);
	
	/**
	 * 将对象转换为Map
	 *  没有考虑对象的父类
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> makeObject2Map(Object obj) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (obj == null) {
			log.info("转换对象为空");
			return paramMap;
		}
		try {
			Class objClass = obj.getClass();
			Method[] methods = objClass.getDeclaredMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					Object targetValueObj = method.invoke(obj);
					if (targetValueObj != null) {
						if (targetValueObj instanceof Integer) {
							if ((Integer) targetValueObj < 0) {
								log.info("方法名：{} 的值小于0",methodName);
								continue;
							}
						} else if (targetValueObj instanceof String 
								&& "".equals(targetValueObj.toString())) {
							//if ("".equals(targetValueObj.toString())) {
								log.info("方法名：{} 的值为空",methodName);
								continue;
							//}
						}
						paramMap.put(methodName.substring(3, 4).toLowerCase()
								+ methodName.substring(4), targetValueObj);
					}
				}
			}
		} catch (Exception e) {
			log.error("将对象转换为Map异常：", e);
		}
		return paramMap;
	}
	/**
	 * 将对象转换为Map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> convertObject2Map(Object obj) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (obj == null) {
			log.info("转换对象为空");
			return paramMap;
		}
		try {
			Class objClass = obj.getClass();
			Method[] methods = objClass.getDeclaredMethods();
			//只考虑直接父级 而且直接父级不是Object
			if(objClass.getSuperclass() != null && !Object.class.equals(objClass.getSuperclass())){
				Method[] superMethodes = objClass.getSuperclass().getDeclaredMethods();
				if(superMethodes != null && superMethodes.length > 0){
					methods = (Method[]) ArrayUtils.addAll(methods, superMethodes);
				}
			}
			for (Method method : methods) {
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					Object targetValueObj = method.invoke(obj);
					if (targetValueObj != null) {
						if (targetValueObj instanceof Integer) {
							if ((Integer) targetValueObj < 0) {
								log.info("方法名：{} 的值小于0",methodName);
								continue;
							}
						} else if (targetValueObj instanceof String
								&& "".equals(targetValueObj.toString())) {
							//if ("".equals(targetValueObj.toString())) {
							log.info("方法名：{} 的值为空",methodName);
							continue;
							//}
						}
						paramMap.put(methodName.substring(3, 4).toLowerCase()
								+ methodName.substring(4), targetValueObj);
					}
				}
			}
		} catch (Exception e) {
			log.error("将对象转换为Map异常：", e);
		}
		return paramMap;
	}
	
	// Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    public static Object transMap2Bean(Map<String, Object> map, Class className) throws Exception {

        Object obj=null;
        try {
            obj=className.newInstance();//传入当前构造函数要的参数列表
            BeanInfo beanInfo = Introspector.getBeanInfo(className);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                 if(StringUtils.isNotBlank(key)){
                     String realKey=propertyToField(key).toLowerCase();
                     if (map.containsKey(realKey)) {
                         Object value = map.get(realKey);
                         // 得到property对应的setter方法
                         Method setter = property.getWriteMethod();
                         setter.invoke(obj, value);
                     }
                 }
            }

        } catch (Exception e) {
			log.error("transMap2Bean Error：", e);
        }
        return obj;

    }
    
    /**
     * 对象属性转换为字段  例如：userName to user_name
     * @param property 字段名
     * @return
     */
    public static String propertyToField(String property) {
        if (null == property) {
            return "";
        }
        char[] chars = property.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : chars) {
            if (CharUtils.isAsciiAlphaUpper(c)) {
                sb.append("_" + StringUtils.lowerCase(CharUtils.toString(c)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
