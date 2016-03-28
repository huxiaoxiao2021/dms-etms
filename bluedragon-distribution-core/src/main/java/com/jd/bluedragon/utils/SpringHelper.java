package com.jd.bluedragon.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class SpringHelper implements ApplicationContextAware {

	private static ApplicationContext applicationContext = null;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (SpringHelper.applicationContext == null) {
			SpringHelper.applicationContext = applicationContext;
		}
    }

	public static ApplicationContext getApplicationContext() {
		return SpringHelper.applicationContext;
    }

	public static Object getBean(String beanName) {
		return SpringHelper.applicationContext.getBean(beanName);
    }

    /**
     * 获取BEAN列表
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeans(Class<T> tClass){
        return SpringHelper.applicationContext.getBeansOfType(tClass);
    }
}
