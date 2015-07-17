package com.jd.bluedragon.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

}
