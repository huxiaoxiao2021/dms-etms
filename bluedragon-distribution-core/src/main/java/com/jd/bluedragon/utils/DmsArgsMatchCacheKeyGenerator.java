package com.jd.bluedragon.utils;

import com.jd.bluedragon.core.base.BaseMajorManagerImpl;
import com.jd.bluedragon.core.base.BaseMinorManagerImpl;
import com.jd.bluedragon.distribution.base.service.impl.BaseServiceImpl;
import com.jd.bluedragon.distribution.base.service.impl.SiteServiceImpl;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.framework.utils.cache.keygenerator.CacheKeyGenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DmsArgsMatchCacheKeyGenerator implements CacheKeyGenerator {

	@Override
	public String generateKey(Method method, Object[] args, Object target,
			String key) {
		if ((key.indexOf("@args") < 0) || (args == null)) {
			return key;
		}
		String result = key;
		for (int i = 0; i < args.length; i++) {
			result = result.replaceAll("@args" + i, String.valueOf(args[i]));
		}
		return result;
	}

	public static void main(String args[]) {
		List<Class> classes = new ArrayList<Class>();
		classes.add(BaseServiceImpl.class);
		classes.add(SiteServiceImpl.class);
		classes.add(BaseMajorManagerImpl.class);
		classes.add(BaseMinorManagerImpl.class);
		for (Class target : classes) {
			System.out.println("Class:" + target.getName()+"\n");
			Method[] methods = target.getMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				Object[] generatedArgs = null;
				Cache annotation = method.getAnnotation(Cache.class);
				if (annotation != null) {
					String key = annotation.key();
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length > 0) {
						generatedArgs = new Object[parameterTypes.length];
						for (int j = 0; j < parameterTypes.length; j++) {
							Class parameterType = parameterTypes[j];
							if (parameterType.equals(String.class)) {
								generatedArgs[j] = "String";
							} else if (parameterType.equals(Short.class)) {
								generatedArgs[j] = 1111;
							} else if (parameterType.equals(Integer.class)) {
								generatedArgs[j] = 2222;
							} else if (parameterType.equals(Long.class)) {
								generatedArgs[j] = 3333;
							}
						}
					}
					String result = new DmsArgsMatchCacheKeyGenerator()
							.generateKey(method, generatedArgs, null, key);
					System.out.println(key + "----->>" + result);
				}

			}
		}
	}

}
