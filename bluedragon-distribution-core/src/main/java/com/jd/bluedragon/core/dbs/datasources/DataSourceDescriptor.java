package com.jd.bluedragon.core.dbs.datasources;

import javax.sql.DataSource;

/**
 * datasource descriptor
 * 
 * @author admin
 * 
 */
public class DataSourceDescriptor {
	private String identity;
	private DataSource mainDataSource;
	private DataSource standbyDataSource;
	private DataSource dataSource;
	private int threadPoolSize = Runtime.getRuntime().availableProcessors() * 5;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public DataSource getMainDataSource() {
		return mainDataSource;
	}

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainDataSource = mainDataSource;
	}

	public DataSource getStandbyDataSource() {
		return standbyDataSource;
	}

	public void setStandbyDataSource(DataSource standbyDataSource) {
		this.standbyDataSource = standbyDataSource;
	}

	public DataSource getDataSource() {
		if (this.dataSource == null) {
			this.dataSource = this.mainDataSource;
		}
		return this.dataSource;

	}

	public void swap() {
		if (this.dataSource == this.mainDataSource) {
			this.dataSource = this.standbyDataSource;
		} else {
			this.dataSource = this.mainDataSource;
		}
	}

	public int getThreadPoolSize() {
		return this.threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

}
