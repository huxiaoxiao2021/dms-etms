package com.jd.bluedragon.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-21 下午09:25:34
 *
 * 类说明
 */
public class ObjectMapHelper {
	
	private final static Log logger = LogFactory.getLog(ObjectMapHelper.class);
	
	/**
	 * 将对象转换为Map
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> makeObject2Map(Object obj) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (obj == null) {
			logger.info("转换对象为空");
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
								logger.info("方法名：" + methodName + " 的值小于0");
								continue;
							}
						} else if (targetValueObj instanceof String 
								&& "".equals(targetValueObj.toString())) {
							//if ("".equals(targetValueObj.toString())) {
								logger.info("方法名：" + methodName + " 的值为空");
								continue;
							//}
						}
						paramMap.put(methodName.substring(3, 4).toLowerCase()
								+ methodName.substring(4), targetValueObj);
					}
				}
			}
		} catch (Exception e) {
			logger.error("将对象转换为Map异常：", e);
		}
		return paramMap;
	}
}
