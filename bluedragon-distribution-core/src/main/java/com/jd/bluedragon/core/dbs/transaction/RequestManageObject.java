package com.jd.bluedragon.core.dbs.transaction;

public class RequestManageObject implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2766707961919598310L;
	private String crud;
	private String statement;
	private Object parameter;
	private String paramClassName;

	private String dataSourceName;

	public String getCrud() {
		return crud;
	}

	public void setCrud(String crud) {
		this.crud = crud;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getParamClassName() {
		return paramClassName;
	}

	public void setParamClassName(String paramClassName) {
		this.paramClassName = paramClassName;
	}

}
