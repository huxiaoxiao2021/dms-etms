package com.jd.bluedragon.core.objectid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service("genObjectId")
public class DbsGenerateObjectId implements IGenerateObjectId {
	private static Map<String, AtomicInteger> nextIncMap = Collections
			.synchronizedMap(new HashMap<String, AtomicInteger>());
	private static Map<String, Long> firstMap = Collections
			.synchronizedMap(new HashMap<String, Long>());
	private DataSource dataSource;
	private PlatformTransactionManager transactionManager;
	private int maxValue = 999;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		transactionManager = new DataSourceTransactionManager(dataSource);
	}

	@Override
	public synchronized long getObjectId(String tableName) {
		if (!firstMap.containsKey(tableName)) {
			firstMap.put(tableName, this.getAndSaveObjectFirstId(tableName, 1));
			nextIncMap.put(tableName, new AtomicInteger(0));
		}
		int lastId = nextIncMap.get(tableName).addAndGet(1);
		if (lastId > maxValue) {
			firstMap.put(tableName, this.getAndSaveObjectFirstId(tableName, 1));
			nextIncMap.put(tableName, new AtomicInteger(1));
			lastId = 1;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(firstMap.get(tableName));

		int idLen = (lastId + "").length();
		int zeroLen = (maxValue + "").length() - idLen;
		for (int j = 0; j < zeroLen; j++) {
			sb.append("0");
		}
		sb.append(lastId);
		long objectId = Long.parseLong(sb.toString());
		return objectId;
	}

	private boolean isOracleDataSource() {
		if (dataSource instanceof BasicDataSource) {
			String url = ((BasicDataSource) dataSource).getUrl();
			if (url.toLowerCase().indexOf("oracle") > 0) {
				return true;
			}
		}
		return false;
	}

	private long getAndSaveObjectFirstId(final String tableName, final int count) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(
				transactionManager);
		transactionTemplate
				.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		long result = transactionTemplate
				.execute(new TransactionCallback<Long>() {
					@Override
					public Long doInTransaction(TransactionStatus status) {
						String sql = "update dbs_objectid set firstId=firstId+"
								+ count + " where objectName=?";
						JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
						int rowCount = jdbcTemplate.update(sql, tableName);
						if (rowCount == 0) {
							if (isOracleDataSource())
								sql = "insert into dbs_objectid(id,objectName,firstId)values(SEQ_DBS_OBJECTID.NEXTVAL,?,?)";
							else
								sql = "insert into dbs_objectid(objectName,firstId)values(?,?)";
							jdbcTemplate.update(sql, tableName, count);
							return 1L;
						} else {
							sql = "select firstId from dbs_objectid where objectName=?";
							return jdbcTemplate.queryForLong(sql, tableName);
						}
					}

				});
		return result;
	}

	public static void main(String[] args) {
		// for (int i = 1; i < 100; i++) {
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// BasicDataSource ds = new BasicDataSource();
		// ds.setDriverClassName("com.mysql.jdbc.Driver");
		// ds.setUrl("jdbc:mysql://localhost:3306/notdbs?characterEncoding=UTF-8");
		// ds.setUsername("root");
		// ds.setPassword("root");
		// ds.setDefaultAutoCommit(false);
		// ds.setMaxActive(1000);
		// ds.setMaxIdle(1000);
		// ds.setMaxWait(1800);
		// DbsGenerateObjectId objId = new DbsGenerateObjectId();
		// objId.setDataSource(ds);
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// System.out.println(objId.getAndSaveObjectFirstId(
		// "test.test.test", 1));
		// }
		//
		// }).start();
		// }
	}
}
