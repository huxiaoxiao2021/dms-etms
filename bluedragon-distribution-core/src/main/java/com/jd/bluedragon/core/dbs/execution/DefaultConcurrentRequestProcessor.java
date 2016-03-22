package com.jd.bluedragon.core.dbs.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.jd.bluedragon.core.dbs.transaction.MutiDataSourcesTransactionMonitor;
import com.jd.bluedragon.core.dbs.util.DbsParameter;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.dao.ConcurrencyFailureException;


/**
 * 
 * request processor
 * 
 * @author lidonginfo
 * 
 */
public class DefaultConcurrentRequestProcessor implements
        IConcurrentRequestProcessor {
	private static Logger logger = Logger
			.getLogger(DefaultConcurrentRequestProcessor.class);

	/**
	 * process all request (non-Javadoc)
	 * 
	 * @see IConcurrentRequestProcessor#process(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
	public List<Object> process(List<ConcurrentRequest> requests) {
		Map<String, List<ConcurrentRequest>> writeMap = new HashMap<String, List<ConcurrentRequest>>();
		Map<String, List<ConcurrentRequest>> selectMap = new HashMap<String, List<ConcurrentRequest>>();
		List<String> dataSourceNames = new ArrayList<String>();
		List<ConcurrentRequest> requestList = new ArrayList<ConcurrentRequest>();
		for (ConcurrentRequest request : requests) {
			if (!dataSourceNames.contains(request.getDataSourceName())) {
				dataSourceNames.add(request.getDataSourceName());
			}
			requestList.addAll(this.selectRequestProcess(request));
		}
		for (ConcurrentRequest request : requestList) {
			if (request.isWriteRequest()) {
				if (!writeMap.containsKey(request.getDataSourceName())) {
					writeMap.put(request.getDataSourceName(),
							new ArrayList<ConcurrentRequest>());
				}
				writeMap.get(request.getDataSourceName()).add(request);

			} else {
				if (request.getParameter() instanceof Map
						&& ((Map) request.getParameter())
								.containsKey(DbsParameter.AllTables)) {
					Map m = (Map) request.getParameter();
					String curTable = m.get(DbsParameter.CurTable).toString();
					if (!selectMap.containsKey(request.getDataSourceName()
							+ "#" + curTable)) {
						selectMap.put(request.getDataSourceName() + "#"
								+ curTable, new ArrayList<ConcurrentRequest>());
					}
					selectMap.get(request.getDataSourceName() + "#" + curTable)
							.add(request);
				} else {
					if (!selectMap.containsKey(request.getDataSourceName())) {
						selectMap.put(request.getDataSourceName(),
								new ArrayList<ConcurrentRequest>());
					}
					selectMap.get(request.getDataSourceName()).add(request);
				}
			}
		}
		if (writeMap.size() > 0)
			return this.innerWriteProcess(writeMap);
		else

			return this.innerSelectProcess(selectMap, dataSourceNames);

	}

	private List<Object> innerWriteProcess(
			Map<String, List<ConcurrentRequest>> map) {
		List<String> dataSourceNames = new ArrayList<String>();
		dataSourceNames.addAll(map.keySet());
		MutiDataSourcesTransactionMonitor
				.openTransactionIfNeed(dataSourceNames);
		List<Object> list = new ArrayList<Object>();
		for (final String dbName : map.keySet()) {
			final List<ConcurrentRequest> requests = map.get(dbName);
			if (requests.size() == 0)
				continue;

			for (ConcurrentRequest request : requests) {
				list.add(innerExecuteWith(request));
			}
		}
		return list;
	}

	private List<Object> innerSelectProcess(
			Map<String, List<ConcurrentRequest>> map,
			List<String> dataSourceNames) {
		List<Object> resultList = new ArrayList<Object>();
		if (map == null || map.size() == 0)
			return resultList;
		MutiDataSourcesTransactionMonitor
				.openTransactionIfNeed(dataSourceNames);
		final CountDownLatch latch = new CountDownLatch(map.size());
		List<Future<List<Object>>> futures = new ArrayList<Future<List<Object>>>();
		for (final String key : map.keySet()) {
			final List<ConcurrentRequest> requests = map.get(key);
			if (requests.size() == 0)
				continue;

			futures.add(requests.get(0).getExecutor()
					.submit(new Callable<List<Object>>() {
						public List<Object> call() throws Exception {
							try {

								List<Object> list = new ArrayList<Object>();
								for (ConcurrentRequest request : requests) {
									list.add(innerExecuteWith(request));
								}
								return list;
							} finally {
								latch.countDown();
							}
						}
					}));
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new ConcurrencyFailureException(
					"interrupted when processing data access request in concurrency",
					e);
		}

		logger.info("begin fillResultListWithFutureResults");
		fillResultListWithFutureListResults(futures, resultList);
		return resultList;
	}

	/**
	 * all sql execute in there
	 * 
	 * @param request
	 * @return
	 */
	public Object executeWith(ConcurrentRequest request) {
		List<String> dataSourceNames = new ArrayList<String>();
		dataSourceNames.add(request.getDataSourceName());
		MutiDataSourcesTransactionMonitor
				.openTransactionIfNeed(dataSourceNames);
		if (request.isWriteRequest()) {
			return this.innerExecuteWith(request);
		} else {
			List<ConcurrentRequest> requests = this
					.selectRequestProcess(request);
			if (requests.size() > 1) {
				return this.process(requests);
			}

		}
		return this.innerExecuteWith(request);

	}

	public Object innerExecuteWith(ConcurrentRequest request) {
		try {

			SqlSession session = request.getSqlSession();
			Object result = null;
			if (request.getCrud().equals("insert")) {
				result = session.insert(request.getStatement(),
						request.getParameter());
				int count = Integer.parseInt(result.toString());
				if (count > 0) {
					MutiDataSourcesTransactionMonitor.pushRequest(request);
				}
			} else if (request.getCrud().equals("update")) {
				result = session.update(request.getStatement(),
						request.getParameter());
				int count = Integer.parseInt(result.toString());
				if (count > 0) {
					MutiDataSourcesTransactionMonitor.pushRequest(request);
				}
			} else if (request.getCrud().equals("delete")) {
				result = session.delete(request.getStatement(),
						request.getParameter());
				int count = Integer.parseInt(result.toString());
				if (count > 0) {
					MutiDataSourcesTransactionMonitor.pushRequest(request);
				}
			} else if (request.getCrud().equals("selectList")) {
				result = session.selectList(request.getStatement(),
						request.getParameter());
			} else if (request.getCrud().equals("selectOne")) {
				result = session.selectOne(request.getStatement(),
						request.getParameter());
			} else if (request.getCrud().equals("selectMap")) {
				result = session.selectMap(request.getStatement(),
						request.getParameter(), request.getMapKey(),
						request.getRowBounds());
			} else
				throw new RuntimeException("can't excetue crud "
						+ request.getCrud());
			// if (request.getExecutorType() == ExecutorType.BATCH)
			// session.commit();
			return result;
		} catch (Throwable t) {
			logger.error(t.getMessage());
			throw new RuntimeException(t);
		} finally {
			// if (session != null)
			// SqlSessionUtils.closeSqlSession(session, this.sessionFactory);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<ConcurrentRequest> selectRequestProcess(
			ConcurrentRequest request) {
		List<ConcurrentRequest> requests = null;
		if (!request.isWriteRequest() && request.getParameter() instanceof Map) {
			Map m = (Map) request.getParameter();
			if (m.containsKey(DbsParameter.AllTables)) {
				if (!m.containsKey(DbsParameter.CurTable)
						|| m.get(DbsParameter.CurTable) == null) {
					String[] allTables = m.get(DbsParameter.AllTables)
							.toString().split("\\#");
					requests = new ArrayList<ConcurrentRequest>();
					for (String tableName : allTables) {
						Map m2 = new HashMap();
						for (Object key : m.keySet()) {
							m2.put(key, m.get(key));
						}
						m2.put(DbsParameter.CurTable, tableName);
						int pos = tableName.lastIndexOf("_");
						if (pos > 0) {
							String tableNo = tableName.substring(pos,
									tableName.length());
							if (m2.containsKey(DbsParameter.JoinTable1)) {
								m2.put(DbsParameter.JoinTable1,
										m2.get(DbsParameter.JoinTable1)
												+ tableNo);
							}
							if (m2.containsKey(DbsParameter.JoinTable2)) {
								m2.put(DbsParameter.JoinTable2,
										m2.get(DbsParameter.JoinTable2)
												+ tableNo);
							}
							if (m2.containsKey(DbsParameter.JoinTable3)) {
								m2.put(DbsParameter.JoinTable3,
										m2.get(DbsParameter.JoinTable3)
												+ tableNo);
							}
							if (m2.containsKey(DbsParameter.JoinTable4)) {
								m2.put(DbsParameter.JoinTable4,
										m2.get(DbsParameter.JoinTable4)
												+ tableNo);
							}
						}
						ConcurrentRequest r = new ConcurrentRequest();
						r.setCrud(request.getCrud());
						r.setDataSourceName(request.getDataSourceName());
						r.setExecutor(request.getExecutor());
						r.setMapKey(request.getMapKey());
						r.setParameter(m2);
						r.setSqlSession(request.getSqlSession());
						r.setStatement(request.getStatement());
						r.setExecutorType(request.getExecutorType());
						r.setRowBounds(request.getRowBounds());
						requests.add(r);
					}
					return requests;
				}
			}
		}
		if (requests == null) {
			requests = new ArrayList<ConcurrentRequest>();
		}
		requests.add(request);
		return requests;

	}

	/**
	 * after Parallel execute,merger
	 * 
	 * @param futures
	 * @param resultList
	 */
	// private void fillResultListWithFutureResults(List<Future<Object>>
	// futures,
	// List<Object> resultList) {
	// for (Future<Object> future : futures) {
	// try {
	// resultList.add(future.get());
	// } catch (InterruptedException e) {
	// throw new ConcurrencyFailureException(
	// "interrupted when processing data access request in concurrency",
	// e);
	// } catch (ExecutionException e) {
	// throw new ConcurrencyFailureException(
	// "something goes wrong in processing", e);
	// }
	// }
	// }

	/**
	 * after Parallel execute,merger
	 * 
	 * @param futures
	 * @param resultList
	 */
	@SuppressWarnings("unchecked")
	private void fillResultListWithFutureListResults(
			List<Future<List<Object>>> futures, List<Object> resultList) {
		try {
			for (Future<List<Object>> future : futures) {

				for (Object obj : future.get()) {
					if (obj instanceof List) {
						List<Object> list = (List<Object>) obj;
						for (Object item : list) {
							resultList.add(item);
						}
					} else
						resultList.add(obj);
				}

			}
		} catch (InterruptedException e) {
			throw new ConcurrencyFailureException(
					"interrupted when processing data access request in concurrency",
					e);
		} catch (ExecutionException e) {
			throw new ConcurrencyFailureException(
					"something goes wrong in processing", e);
		}
	}
}
