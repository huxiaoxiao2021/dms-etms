package com.jd.bluedragon.utils;

import com.jd.bluedragon.core.base.BaseMajorManagerImpl;
import com.jd.bluedragon.core.base.BaseMinorManagerImpl;
import com.jd.bluedragon.distribution.base.service.impl.BaseServiceImpl;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.framework.utils.cache.keygenerator.CacheKeyGenerator;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CacheKeyGeneratorScaner implements InitializingBean {

	private CacheKeyGenerator keyGenerator;
	@SuppressWarnings("rawtypes")
	private List<Class> classes = new ArrayList<Class>();

	private static final String SIMULATE_STRING_VALUE = "String";
	private static final Short SIMULATE_SHORT_VALUE = 1111;
	private static final Integer SIMULATE_INTEGER_VALUE = 2222;
	private static final Long SIMULATE_LONG_VALUE = 3333L;

	public void setKeyGenerator(CacheKeyGenerator keyGenerator) {
		this.keyGenerator = keyGenerator;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (classes == null) {
			addScanedClasses();
		}
		if (keyGenerator != null && classes != null) {
			scan(keyGenerator, classes);
		}
	}

	protected void addScanedClasses() {
		classes.add(BaseMajorManagerImpl.class);
		classes.add(BaseMinorManagerImpl.class);
		classes.add(BaseServiceImpl.class);
	}

	private void scan(CacheKeyGenerator keyGenerator, List<Class> classes) {
		Set<String> keys = new HashSet<String>();
		for (Class<?> target : classes) {
			try {
				System.out.println("Scan Class Begin:" + target.getName());
				Method[] methods = target.getMethods();
				for (int i = 0; i < methods.length; i++) {
					Method method = methods[i];
					Object[] generatedArgs = null;
					Cache annotation = method.getAnnotation(Cache.class);
					if (annotation != null) {
						String key = annotation.key();
						if (keys.contains(key)) {
							System.out.println("严重警告：缓存健值 [" + key + "] 重复");
						} else {
							keys.add(key);
						}
						Class<?>[] parameterTypes = method.getParameterTypes();
						if (parameterTypes.length > 0) {
							generatedArgs = new Object[parameterTypes.length];
							for (int j = 0; j < parameterTypes.length; j++) {
								Class<?> parameterType = parameterTypes[j];
								if (parameterType.equals(String.class)) {
									generatedArgs[j] = SIMULATE_STRING_VALUE;
								} else if (parameterType.equals(Short.class)) {
									generatedArgs[j] = SIMULATE_SHORT_VALUE;
								} else if (parameterType.equals(Integer.class)) {
									generatedArgs[j] = SIMULATE_INTEGER_VALUE;
								} else if (parameterType.equals(Long.class)) {
									generatedArgs[j] = SIMULATE_LONG_VALUE;
								} else {
									generatedArgs[j] = SIMULATE_STRING_VALUE;
								}
							}
						}
						String result = keyGenerator.generateKey(method,
								generatedArgs, null, key);
						System.out.println(key + "----->>" + result);
					}

					System.out.println("Scan Class Begin:" + target.getName()
							+ "\n");
				}
			} catch (Exception e) {
				System.out.println("扫描缓存文件：" + e.getMessage());
			}
		}
	}

}
