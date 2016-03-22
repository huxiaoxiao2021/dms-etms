package com.jd.bluedragon.core.dbs.execution;

import java.util.concurrent.ExecutorService;

//import javax.sql.DataSource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

public class ConcurrentRequest {
	// private DataSource dataSource;
	private SqlSession sqlSession;
	/**
	 * thread pool, if null use current thread,not null use executor
	 */
	private ExecutorService executor;
	/**
	 * crud include insert delete update selectOne selectList etc;
	 */
	private String crud;
	private ExecutorType executorType = ExecutorType.SIMPLE;
	private String statement;
	private Object parameter;
	private String mapKey;
	private RowBounds rowBounds = RowBounds.DEFAULT;
	private String dataSourceName;

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getMapKey() {
		return mapKey;
	}

	public void setMapKey(String mapKey) {
		this.mapKey = mapKey;
	}

	public RowBounds getRowBounds() {
		return rowBounds;
	}

	public void setRowBounds(RowBounds rowBounds) {
		this.rowBounds = rowBounds;
	}

	public ExecutorType getExecutorType() {
		return executorType;
	}

	public void setExecutorType(ExecutorType executorType) {
		this.executorType = executorType;
	}

	public String getCrud() {
		return crud;
	}

	public void setCrud(String crud) {
		this.crud = crud;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	public SqlSession getSqlSession() {
		return this.sqlSession;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	// public DataSource getDataSource() {
	// return dataSource;
	// }
	//
	// public void setDataSource(DataSource dataSource) {
	// this.dataSource = dataSource;
	// }

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

	public boolean isWriteRequest() {
		if (this.getCrud().equalsIgnoreCase("insert")
				|| this.getCrud().equalsIgnoreCase("update")
				|| this.getCrud().equalsIgnoreCase("delete")) {
			return true;
		}
		return false;
	}
}
