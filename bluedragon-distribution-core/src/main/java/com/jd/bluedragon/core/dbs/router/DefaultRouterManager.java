package com.jd.bluedragon.core.dbs.router;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

/**
 * router manager default impl
 * 
 * @author admin
 * 
 */
public class DefaultRouterManager implements IRouterManager, InitializingBean {
	private Set<IRouterStrategy> routers = new HashSet<IRouterStrategy>();
	private Map<String, IRouterStrategy> routerMap = new HashMap<String, IRouterStrategy>();

	@Override
	public Set<IRouterStrategy> getRouters() {
		return this.routers;
	}

	@Override
	public void setRouters(Set<IRouterStrategy> routers) {
		this.routers = routers;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		for (IRouterStrategy router : this.getRouters()) {
			this.routerMap.put(router.getMapperNameSpace(), router);
		}
	}

	@Override
	public String getRouterResult(String statement, Object parameter,
			boolean isWriteRequest) {
		if (isWriteRequest) {
			if (parameter == null || statement == null) {
				throw new NullPointerException();
			}
		}
		int pos = statement.lastIndexOf(".");
		String mapperNameSpace = statement.substring(0, pos);
		IRouterStrategy strategy = routerMap.get(mapperNameSpace);
		if (isWriteRequest)
			return strategy.getWriteRouter(statement, parameter);
		else
			return strategy.getSelectRouter(statement, parameter);
	}

	// /**
	// * ��ȫƥ���·�ɹ���
	// *
	// * @param statement
	// * @param paramValue
	// * @return
	// */
	// private String getFullMatchRouterResult(String statement, Object value) {
	// if (this.getRouters() == null)
	// return null;
	// String result = null;
	// IRouterStrategy strategy = null;
	//
	// if (value != null) {
	// strategy = routerMap.get(value.getClass().getName());
	// }
	// if (strategy == null && statement != null) {
	// strategy = routerMap.get(statement);
	// }
	// if (strategy != null) {
	// result = strategy.getRouterResult(value);
	// if (result == null) {
	// throw new RuntimeException("getFullMatchRouterResult-"
	// + value.getClass().getName() + "��ʵ��·�ɲ��ԣ�");
	// }
	// }
	// return result;
	// }

	/**
	 * ����ƥ��·�ɹ���
	 * 
	 * @param statement
	 * @param paramValue
	 * @return
	 */
	// private String getRegMatchRouterResult(String statement, Object value) {
	// if (this.getRouters() == null)
	// return null;
	// String result = null;
	// IRouterStrategy strategy = null;
	//
	// if (value != null) {
	// String ruleKey = null;
	// for (String key : routerMap.keySet()) {
	// if (value.getClass().getName()
	// .indexOf(key.substring(0, key.length() - 1)) >= 0) {
	// ruleKey = value.getClass().getName();
	// break;
	// }
	// }
	// strategy = routerMap.get(ruleKey);
	// }
	// if (strategy == null && statement != null) {
	// String ruleKey = null;
	// for (String key : routerMap.keySet()) {
	// if (statement.indexOf(key.substring(0, key.length() - 1)) >= 0) {
	// ruleKey = statement;
	// break;
	// }
	// }
	// strategy = routerMap.get(ruleKey);
	// }
	// if (strategy != null) {
	// result = strategy.getRouterResult(value);
	// if (result == null) {
	// throw new RuntimeException("getRegMatchRouterResult-"
	// + statement + "��ʵ��·�ɲ��ԣ�");
	// }
	// }
	// return result;
	// }

}
