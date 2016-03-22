package com.jd.bluedragon.core.dbs.router;

/**
 * Router interface getRouterRule:object.className,statement
 * getRouterResult:dataSource id
 * 
 * @author admin
 * 
 */
public interface IRouterStrategy {
	String getMapperNameSpace();

	String getSelectRouter(String statement, Object parameter);

	String getWriteRouter(String statement, Object parameter);

}
