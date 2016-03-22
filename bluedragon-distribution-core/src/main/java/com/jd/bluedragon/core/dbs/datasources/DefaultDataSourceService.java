package com.jd.bluedragon.core.dbs.datasources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.jd.bluedragon.core.dbs.router.IRouterManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;


/**
 * datasource manager service default impl
 * 
 * @author admin
 * 
 */
public class DefaultDataSourceService implements IDataSourceService,
		InitializingBean {
	private Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
	private Set<DataSourceDescriptor> dataSourceDescriptors = new HashSet<DataSourceDescriptor>();
	private Map<String, DataSourceDescriptor> dataSourceMap = new HashMap<String, DataSourceDescriptor>();
	private IRouterManager routerManager;
	private String defaultIdentity;

	public void setDataSourceDescriptors(
			Set<DataSourceDescriptor> dataSourceDescriptors) {
		this.dataSourceDescriptors = dataSourceDescriptors;
	}

	@Override
	public Set<DataSourceDescriptor> getDataSourceDescriptors() {
		return dataSourceDescriptors;
	}

	@Override
	public Map<String, DataSource> getDataSources() {
		return dataSources;
	}

	public String getDefaultIdentity() {
		return this.defaultIdentity;
	}

	@Override
	public void setDefaultIdentity(String defaultIdentity) {
		this.defaultIdentity = defaultIdentity;
	}

	public IRouterManager getRouterManager() {
		return routerManager;
	}

	public void setRouterManager(IRouterManager routerManager) {
		this.routerManager = routerManager;
	}

	@Override
	public DataSourceDescriptor getDataSourceDescriptor(String identity) {
		return dataSourceMap.get(identity);
	}

	public void swapDataSource(String identity) {
		if (!this.dataSourceMap.containsKey(identity))
			return;
		this.dataSourceMap.get(identity).swap();
		this.dataSources.put(identity, this.dataSourceMap.get(identity)
				.getDataSource());
	}

	public void afterPropertiesSet() throws Exception {

		initDataSources();
	}

	private void initDataSources() {
		for (DataSourceDescriptor desc : this.getDataSourceDescriptors()) {
			DataSource dataSourceToUse = desc.getMainDataSource();
			desc.setMainDataSource(new LazyConnectionDataSourceProxy(
					dataSourceToUse));

			dataSourceToUse = desc.getStandbyDataSource();
			if (dataSourceToUse != null) {
				desc.setStandbyDataSource(new LazyConnectionDataSourceProxy(
						dataSourceToUse));
			}
			dataSources.put(desc.getIdentity(), desc.getDataSource());
			dataSourceMap.put(desc.getIdentity(), desc);
		}
	}

}
