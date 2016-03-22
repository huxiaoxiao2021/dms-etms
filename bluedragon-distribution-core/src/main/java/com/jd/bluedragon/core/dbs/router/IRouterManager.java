package com.jd.bluedragon.core.dbs.router;


import java.util.Set;

/**
 * Router manager interface
 * 
 * @author admin
 * 
 */
public interface IRouterManager {
	Set<IRouterStrategy> getRouters();

	void setRouters(Set<IRouterStrategy> routers);

	String getRouterResult(String statement, Object parameter, boolean isWriteRequest);
}
