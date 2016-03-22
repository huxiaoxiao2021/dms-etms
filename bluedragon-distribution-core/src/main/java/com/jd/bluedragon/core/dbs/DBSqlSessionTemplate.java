package com.jd.bluedragon.core.dbs;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import com.jd.bluedragon.core.dbs.datasources.DataSourceDescriptor;
import com.jd.bluedragon.core.dbs.datasources.IDataSourceService;
import com.jd.bluedragon.core.dbs.execution.ConcurrentRequest;
import com.jd.bluedragon.core.dbs.execution.DefaultConcurrentRequestProcessor;
import com.jd.bluedragon.core.dbs.execution.IConcurrentRequestProcessor;
import com.jd.bluedragon.core.dbs.util.MapUtils;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.dao.IncorrectResultSizeDataAccessException;


/**
 * sql executor
 * 
 * @author admin
 * 
 */
@SuppressWarnings("rawtypes")
public class DBSqlSessionTemplate implements SqlSession, InitializingBean,
		DisposableBean {
	private static Logger logger = Logger.getLogger(DBSqlSessionTemplate.class);
	private static final String DEFAULT_DATASOURCE_IDENTITY = "_DbSqlSessionTemplate_default_data_source_name";
	private IDataSourceService dataSourceService;
	private boolean profileLongTimeRunningSql = false;
	private long longTimeRunningSqlIntervalThreshold;
	/**
	 * setup ExecutorService for data access requests on each data sources.<br>
	 * map key(String) is the identity of DataSource; map value(ExecutorService)
	 * is the ExecutorService that will be used to execute query requests on the
	 * key's data source.
	 */
	private Map<String, ExecutorService> dataSourceSpecificExecutors = new HashMap<String, ExecutorService>();
	private IConcurrentRequestProcessor concurrentRequestProcessor = new DefaultConcurrentRequestProcessor();
	private Map<String, SqlSession> sessionMap = new HashMap<String, SqlSession>();
	private Resource configLocation;

	public DBSqlSessionTemplate(IDataSourceService dataSourceService,
			String defaultKey) {
		this.dataSourceService = dataSourceService;
		if (defaultKey != null)
			this.dataSourceService.setDefaultIdentity(defaultKey);
	}

	public IDataSourceService getDataSourceService() {
		return this.dataSourceService;
	}

	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	public void setProfileLongTimeRunningSql(boolean profileLongTimeRunningSql) {
		this.profileLongTimeRunningSql = profileLongTimeRunningSql;
	}

	public boolean isProfileLongTimeRunningSql() {
		return profileLongTimeRunningSql;
	}

	public void setLongTimeRunningSqlIntervalThreshold(
			long longTimeRunningSqlIntervalThreshold) {
		this.longTimeRunningSqlIntervalThreshold = longTimeRunningSqlIntervalThreshold;
	}

	public long getLongTimeRunningSqlIntervalThreshold() {
		return longTimeRunningSqlIntervalThreshold;
	}

	public Map<String, ExecutorService> getDataSourceSpecificExecutors() {
		return dataSourceSpecificExecutors;
	}

	/**
	 * if a router and a data source locator is provided, it means data access
	 * on different databases is enabled.<br>
	 */
	protected boolean isPartitioningBehaviorEnabled() {
		return ((this.getDataSourceService() != null) && (this
				.getDataSourceService().getRouterManager() != null));
	}

	@Override
	public Object selectOne(String statement) {
		return this.selectOne(statement, null);
	}

	@Override
	public Object selectOne(String statement, Object parameter) {
		if (isPartitioningBehaviorEnabled()) {
			SortedMap<String, DataSource> resultDataSources = lookupDataSourcesByRouter(
					statement, parameter, "selectOne");
			if (!MapUtils.isEmpty(resultDataSources)) {
				List<Object> results = executeInConcurrency(statement,
						parameter, resultDataSources, "selectOne");
				if (results.size() > 1)
					throw new IncorrectResultSizeDataAccessException(1);
				return results.get(0);
			}
		}
		return this.getDefaultSession().selectOne(statement, parameter);
	}

	@Override
	public List selectList(String statement) {
		return this.selectList(statement, null);
	}

	@Override
	public List selectList(String statement, Object parameter) {
		return this.selectList(statement, parameter, RowBounds.DEFAULT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List selectList(String statement, Object parameter,
			RowBounds rowBounds) {
		if (isPartitioningBehaviorEnabled()) {
			SortedMap<String, DataSource> resultDataSources = lookupDataSourcesByRouter(
					statement, parameter, "selectList");
			if (!MapUtils.isEmpty(resultDataSources)) {
				List<Object> exeResults = executeInConcurrency(statement,
						parameter, resultDataSources, "selectList");
				List<Object> results = new ArrayList<Object>();
				for (Object er : exeResults) {
					if (er instanceof List) {
						List<Object> temp = (List<Object>) er;
						for (Object o : temp) {
							results.add(o);
						}
					} else {
						results.add(er);
					}
				}
				return results;
			}
		}
		return this.getDefaultSession().selectList(statement, parameter,
				rowBounds);
	}

	@Override
	public Map selectMap(String statement, String mapKey) {
		return this.selectMap(statement, null, mapKey);
	}

	@Override
	public Map selectMap(String statement, Object parameter, String mapKey) {
		return this.selectMap(statement, parameter, mapKey, RowBounds.DEFAULT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map selectMap(String statement, Object parameter, String mapKey,
			RowBounds rowBounds) {
		if (isPartitioningBehaviorEnabled()) {
			SortedMap<String, DataSource> resultDataSources = lookupDataSourcesByRouter(
					statement, parameter, "selectMap");
			if (!MapUtils.isEmpty(resultDataSources)) {
				List<Object> exeResults = executeInConcurrencyMap(statement,
						parameter, mapKey, rowBounds, resultDataSources);
				Map<Object, Object> results = new HashMap<Object, Object>();
				for (Object er : exeResults) {
					Map<Object, Object> temp = (Map<Object, Object>) er;
					for (Object key : temp.keySet()) {
						results.put(key, temp.get(key));
					}
				}
				return results;
			}
		}
		return this.getDefaultSession().selectMap(statement, parameter, mapKey,
				rowBounds);
	}

	@Override
	public void select(String statement, Object parameter, ResultHandler handler) {
		this.select(statement, parameter, null, handler);
	}

	@Override
	public void select(String statement, ResultHandler handler) {
		this.select(statement, null, handler);
	}

	@Override
	public void select(String statement, Object parameter, RowBounds rowBounds,
			ResultHandler handler) {
		throw new UnsupportedOperationException(
				"Manual commit is not allowed over a Spring managed SqlSession");
	}

	@Override
	public int insert(String statement) {

		return this.insert(statement, null);
	}

	@Override
	public int insert(String statement, Object parameter) {
		if (isPartitioningBehaviorEnabled()) {
			List<Object> results = null;
			if (parameter != null && parameter instanceof Collection<?>) {
				results = this.batchExecuteInConcurrency(statement, parameter,
						"insert");
			} else {
				SortedMap<String, DataSource> resultDataSources = lookupDataSourcesByRouter(
						statement, parameter, "insert");
				if (!MapUtils.isEmpty(resultDataSources)) {
					results = executeInConcurrency(statement, parameter,
							resultDataSources, "insert");
				} else {
					return this.getDefaultSession()
							.insert(statement, parameter);
				}
			}
			Integer rowAffacted = 0;

			for (Object item : results) {
				rowAffacted += (Integer) item;
			}
			return rowAffacted;
		}
		// return this.getDefaultSession().insert(statement, parameter);
		Object result = executeDefaultConcurrency(statement, parameter,
				"insert");
		if (result == null)
			return 0;
		else
			return (Integer) result;
	}

	public SqlSession getDefaultSession() {
		if (this.getDataSourceService() == null)
			throw new RuntimeException("dataSourceService can't null");
		if (this.getDataSourceService().getDefaultIdentity() == null)
			throw new RuntimeException(
					"dataSourceService.defaultIdentity can't null");
		return this.sessionMap.get(this.getDataSourceService()
				.getDefaultIdentity());

	}

	@Override
	public int update(String statement) {
		return this.update(statement, null);
	}

	@Override
	public int update(String statement, Object parameter) {
		if (isPartitioningBehaviorEnabled()) {
			List<Object> results = null;
			if (parameter != null && parameter instanceof Collection<?>) {
				results = this.batchExecuteInConcurrency(statement, parameter,
						"update");
			} else {
				SortedMap<String, DataSource> resultDataSources = lookupDataSourcesByRouter(
						statement, parameter, "update");
				if (!MapUtils.isEmpty(resultDataSources)) {
					results = executeInConcurrency(statement, parameter,
							resultDataSources, "update");
				} else {
					return this.getDefaultSession()
							.update(statement, parameter);
				}
			}
			Integer rowAffacted = 0;
			for (Object item : results) {
				rowAffacted += (Integer) item;
			}
			return rowAffacted;
		}
		// return this.getDefaultSession().update(statement, parameter);
		Object result = executeDefaultConcurrency(statement, parameter,
				"update");
		if (result == null)
			return 0;
		else
			return (Integer) result;
	}

	@Override
	public int delete(String statement) {
		return this.delete(statement, null);
	}

	@Override
	public int delete(String statement, Object parameter) {
		if (isPartitioningBehaviorEnabled()) {
			List<Object> results = null;
			if (parameter != null && parameter instanceof Collection<?>) {
				results = this.batchExecuteInConcurrency(statement, parameter,
						"delete");
			} else {
				SortedMap<String, DataSource> resultDataSources = lookupDataSourcesByRouter(
						statement, parameter, "delete");
				if (!MapUtils.isEmpty(resultDataSources)) {
					results = executeInConcurrency(statement, parameter,
							resultDataSources, "delete");

				} else {
					return this.getDefaultSession()
							.delete(statement, parameter);
				}
			}
			Integer rowAffacted = 0;

			for (Object item : results) {
				rowAffacted += (Integer) item;
			}
			return rowAffacted;
		}
		// return this.getDefaultSession().delete(statement, parameter);
		Object result = executeDefaultConcurrency(statement, parameter,
				"delete");
		if (result == null)
			return 0;
		else
			return (Integer) result;
	}

	@Override
	public void commit() {
		throw new UnsupportedOperationException(
				"Manual commit is not allowed over a Spring managed SqlSession");

	}

	@Override
	public void commit(boolean force) {
		throw new UnsupportedOperationException(
				"Manual commit is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void rollback() {
		throw new UnsupportedOperationException(
				"Manual rollback is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void rollback(boolean force) {
		throw new UnsupportedOperationException(
				"Manual rollback is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException(
				"Manual close is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void clearCache() {
		throw new UnsupportedOperationException(
				"Manual clearCache is not allowed over a Spring managed SqlSession");
	}

	@Override
	public Configuration getConfiguration() {
		throw new UnsupportedOperationException(
				"Manual getConfiguration is not allowed over a Spring managed SqlSession");
	}

	@Override
	public <T> T getMapper(Class<T> type) {
		throw new UnsupportedOperationException(
				"Manual getMapper is not allowed over a Spring managed SqlSession");
	}

	@Override
	public Connection getConnection() {
		throw new UnsupportedOperationException(
				"Manual getConnection is not allowed over a Spring managed SqlSession");
	}

	@Override
	public List<BatchResult> flushStatements() {
		throw new UnsupportedOperationException(
				"Manual flushStatements is not allowed over a Spring managed SqlSession");
	}

	protected SortedMap<String, DataSource> lookupDataSourcesByRouter(
			String statementName, Object parameterObject, String crud) {

		SortedMap<String, DataSource> resultMap = new TreeMap<String, DataSource>();
		if (this.getDataSourceService().getRouterManager() == null)
			return resultMap;
		String result = this
				.getDataSourceService()
				.getRouterManager()
				.getRouterResult(statementName, parameterObject,
						this.isWriteReqeust(crud));
		if (result != null) {
			if (result.indexOf(",") > 0) {
				String[] temps = result.split(",");
				for (String temp : temps) {
					DataSourceDescriptor dsDesc = this.getDataSourceService()
							.getDataSourceDescriptor(temp);
					resultMap.put(temp, dsDesc.getDataSource());
				}

			} else {
				DataSourceDescriptor dsDesc = this.getDataSourceService()
						.getDataSourceDescriptor(result);
				resultMap.put(result, dsDesc.getDataSource());
			}
		} else {
			if (this.isWriteReqeust(crud)) {
				throw new RuntimeException("����ݿ��������Ϊ" + crud + "��"
						+ statementName + "����ʵ��·�ɲ��ԣ�");
			}
			Map<String, DataSource> map = getDataSourceService()
					.getDataSources();
			for (String dsName : map.keySet()) {
				resultMap.put(dsName, map.get(dsName));
			}
		}
		return resultMap;

	}

	private boolean isWriteReqeust(String crud) {
		if (crud.equals("update") || crud.equals("insert")
				|| crud.equals("delete")) {
			return true;
		}
		return false;
	}

	/**
	 * sqlִ��
	 * 
	 * @param statement
	 * @param parameter
	 * @param dsMap
	 * @param crud
	 * @return
	 */
	public List<Object> executeInConcurrencyMap(String statement,
			Object parameter, String mapKey, RowBounds rowBounds,
			SortedMap<String, DataSource> dsMap) {
		List<ConcurrentRequest> requests = new ArrayList<ConcurrentRequest>();
		long startTimestamp = System.currentTimeMillis();
		try {
			for (Map.Entry<String, DataSource> entry : dsMap.entrySet()) {
				ConcurrentRequest request = new ConcurrentRequest();
				request.setStatement(statement);
				request.setParameter(parameter);
				request.setSqlSession(this.sessionMap.get(entry.getKey()));
				request.setDataSourceName(entry.getKey());
				request.setCrud("selectMap");
				request.setMapKey(mapKey);
				request.setRowBounds(rowBounds);
				request.setExecutor(getDataSourceSpecificExecutors().get(
						entry.getKey()));
				requests.add(request);
			}
			List<Object> results = this.concurrentRequestProcessor
					.process(requests);

			return results;
		} finally {
			if (isProfileLongTimeRunningSql()) {
				long interval = System.currentTimeMillis() - startTimestamp;
				if (interval > getLongTimeRunningSqlIntervalThreshold()) {
					logger.warn(String
							.format("SQL Statement [{}] with parameter object [{}] ran out of the normal time range, it consumed [{}] milliseconds.",
									new Object[] { statement, parameter,
											interval }));
				}
			}
		}

	}

	/**
	 * ����ִ��sql
	 * 
	 * @param statement
	 * @param parameter
	 * @return
	 */
	public List<Object> batchExecuteInConcurrency(final String statement,
			Object parameter, String crud) {
		if (parameter == null)
			return null;
		Collection<?> paramCol = (Collection<?>) parameter;
		Map<String, List<Object>> parameterMap = new HashMap<String, List<Object>>();
		Iterator<?> iter = paramCol.iterator();
		while (iter.hasNext()) {
			Object paramObject = iter.next();
			SortedMap<String, DataSource> dsMap = lookupDataSourcesByRouter(
					statement, paramObject, crud);
			for (Map.Entry<String, DataSource> entry : dsMap.entrySet()) {
				if (!parameterMap.containsKey(entry.getKey())) {
					parameterMap.put(entry.getKey(), new ArrayList<Object>());
				}
				parameterMap.get(entry.getKey()).add(paramObject);
			}
		}
		List<ConcurrentRequest> requests = new ArrayList<ConcurrentRequest>();
		for (String dsName : parameterMap.keySet()) {
			ConcurrentRequest request = new ConcurrentRequest();
			request.setSqlSession(this.sessionMap.get(dsName));
			request.setDataSourceName(dsName);
			request.setStatement(statement);
			request.setParameter(parameterMap.get(dsName));
			request.setCrud(crud);
			// request.setExecutorType(ExecutorType.BATCH);
			request.setExecutor(getDataSourceSpecificExecutors().get(dsName));
			requests.add(request);
		}
		return this.concurrentRequestProcessor.process(requests);
	}

	/**
	 * ȱʡ���Դ��ִ��
	 * 
	 * @param statement
	 * @param parameter
	 * @param rowBounds
	 * @param crud
	 * @return
	 */
	private Object executeDefaultConcurrency(String statement,
			Object parameter, String crud) {
		ConcurrentRequest request = new ConcurrentRequest();
		request.setStatement(statement);
		request.setParameter(parameter);
		request.setSqlSession(this.getDefaultSession());
		request.setDataSourceName(this.getDataSourceService()
				.getDefaultIdentity());
		request.setExecutor(getDataSourceSpecificExecutors().get(
				request.getDataSourceName()));
		request.setCrud(crud);
		return this.concurrentRequestProcessor.executeWith(request);
	}

	private List<Object> executeInConcurrency(String statement,
			Object parameter, RowBounds rowBounds,
			SortedMap<String, DataSource> dsMap, String crud) {
		List<ConcurrentRequest> requests = new ArrayList<ConcurrentRequest>();
		long startTimestamp = System.currentTimeMillis();
		try {

			if (dsMap.size() > 1) {
				for (Map.Entry<String, DataSource> entry : dsMap.entrySet()) {
					ConcurrentRequest request = new ConcurrentRequest();
					request.setStatement(statement);
					request.setParameter(parameter);
					request.setRowBounds(rowBounds);
					request.setSqlSession(this.sessionMap.get(entry.getKey()));
					request.setDataSourceName(entry.getKey());
					request.setCrud(crud);
					request.setExecutor(getDataSourceSpecificExecutors().get(
							entry.getKey()));
					requests.add(request);
				}
				List<Object> results = this.concurrentRequestProcessor
						.process(requests);
				return results;
			} else {
				String dsName = dsMap.firstKey();
				ConcurrentRequest request = new ConcurrentRequest();
				request.setStatement(statement);
				request.setParameter(parameter);
				request.setRowBounds(rowBounds);
				request.setSqlSession(this.sessionMap.get(dsName));
				request.setDataSourceName(dsName);
				request.setExecutor(getDataSourceSpecificExecutors()
						.get(dsName));
				request.setCrud(crud);
				Object result = this.concurrentRequestProcessor
						.executeWith(request);
				List<Object> results = new ArrayList<Object>();
				results.add(result);
				return results;
			}

		} finally {
			if (isProfileLongTimeRunningSql()) {
				long interval = System.currentTimeMillis() - startTimestamp;
				if (interval > getLongTimeRunningSqlIntervalThreshold()) {
					logger.warn(String
							.format("SQL Statement [{}] with parameter object [{}] ran out of the normal time range, it consumed [{}] milliseconds.",
									new Object[] { statement, parameter,
											interval }));
				}
			}
		}
	}

	/**
	 * sqlִ��
	 * 
	 * @param statement
	 * @param parameter
	 * @param dsMap
	 * @param crud
	 * @return
	 */
	private List<Object> executeInConcurrency(String statement,
			Object parameter, SortedMap<String, DataSource> dsMap, String crud) {

		return this.executeInConcurrency(statement, parameter, null, dsMap,
				crud);
	}

	@Override
	public void destroy() throws Exception {
		if (dataSourceSpecificExecutors != null
				&& dataSourceSpecificExecutors.size() > 0) {
			logger.info("shutdown executors of DBSqlSessionTemplate...");
			for (ExecutorService executor : dataSourceSpecificExecutors
					.values()) {
				if (executor != null) {
					try {
						executor.shutdown();
						executor.awaitTermination(5, TimeUnit.MINUTES);
						executor = null;
					} catch (InterruptedException e) {
						logger.warn(
								"interrupted when shuting down the query executor:\n{}",
								e);
					}
				}
			}
			getDataSourceSpecificExecutors().clear();
			logger.info("all of the executor services in DBSqlSessionTemplate are disposed.");
		}
	}

	public SqlSession getSqlSession(String identity) {
		if (this.sessionMap != null) {
			return this.sessionMap.get(identity);
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (isProfileLongTimeRunningSql()) {
			if (longTimeRunningSqlIntervalThreshold <= 0) {
				throw new IllegalArgumentException(
						"'longTimeRunningSqlIntervalThreshold' should have a positive value if 'profileLongTimeRunningSql' is set to true");
			}
		}
		Map<String, DataSource> map = getDataSourceService().getDataSources();
		for (String dsName : map.keySet()) {
			SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
			factoryBean.setDataSource(map.get(dsName));
			factoryBean.setConfigLocation(this.configLocation);
            DataSource ds=map.get(dsName);
			SpringManagedTransactionFactory transactionFactory = new SpringManagedTransactionFactory(ds);
			factoryBean.setTransactionFactory(transactionFactory);
			SqlSessionFactory sessionFacotry = factoryBean.getObject();
			SqlSessionTemplate sqlSession = new SqlSessionTemplate(
					sessionFacotry);
			this.sessionMap.put(dsName, sqlSession);

			ExecutorService executor = createExecutorForSpecificDataSource(dsName);
			getDataSourceSpecificExecutors().put(dsName, executor);
		}
	}

	private ExecutorService createCustomExecutorService(int poolSize,
			final String method) {
		int coreSize = Runtime.getRuntime().availableProcessors();
		if (poolSize < coreSize) {
			coreSize = poolSize;
		}
		ThreadFactory tf = new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r,
						"thread created at DBSqlSessionTemplate method ["
								+ method + "]");
				t.setDaemon(true);
				return t;
			}
		};
		BlockingQueue<Runnable> queueToUse = new LinkedBlockingQueue<Runnable>();
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize,
				poolSize, 60, TimeUnit.SECONDS, queueToUse, tf,
				new ThreadPoolExecutor.CallerRunsPolicy());

		return executor;
	}

	private ExecutorService createExecutorForSpecificDataSource(
			String dataSourceName) {
		final String identity = dataSourceName;

		final ExecutorService executor = createCustomExecutorService(
				this.dataSourceService.getDataSourceDescriptor(dataSourceName)
						.getThreadPoolSize(), DEFAULT_DATASOURCE_IDENTITY
						+ identity + " data source");
		// 1. register executor for disposing explicitly
		// internalExecutorServiceRegistry.add(executor);
		// 2. dispose executor implicitly
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (executor == null) {
					return;
				}

				try {
					executor.shutdown();
					executor.awaitTermination(5, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					logger.warn(
							"interrupted when shuting down the query executor:\n{}",
							e);
				}
			}
		});
		return executor;
	}

}
