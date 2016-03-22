package com.jd.bluedragon.core.dbs.datasources;

import com.jd.bluedragon.core.dbs.router.IRouterManager;

import java.util.Set;
import java.util.Map;

import javax.sql.DataSource;


/**
 * datasource manager service
 * 
 * @author admin
 * 
 */
public interface IDataSourceService {
	Map<String, DataSource> getDataSources();

	String getDefaultIdentity();

	void setDefaultIdentity(String defaultIdentity);

	DataSourceDescriptor getDataSourceDescriptor(String identity);

	Set<DataSourceDescriptor> getDataSourceDescriptors();

	void swapDataSource(String identity);

	IRouterManager getRouterManager();

	void setRouterManager(IRouterManager routerManager);

}
