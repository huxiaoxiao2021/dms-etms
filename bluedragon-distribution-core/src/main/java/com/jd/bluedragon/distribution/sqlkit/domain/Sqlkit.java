package com.jd.bluedragon.distribution.sqlkit.domain;

public class Sqlkit implements java.io.Serializable{
	private static final long serialVersionUID = 912899558209868233L;
	private String dataSourceName;
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public String getSqlContent() {
		return sqlContent;
	}
	public void setSqlContent(String sqlContent) {
		this.sqlContent = sqlContent;
	}
	private String sqlContent;
}
