package com.jd.bluedragon.core.dbs.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.jd.bluedragon.core.dbs.datasources.IDataSourceService;
import com.jd.bluedragon.core.dbs.transaction.trigger.TransactionManagerTrigger;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * muti dataSource transaction manager implements PlatformTransactionManager
 * 
 * @author admin
 * 
 */
public class MultiDataSourcesTransactionManager implements
		PlatformTransactionManager, InitializingBean, DisposableBean {
	private static Logger logger = Logger
			.getLogger(MultiDataSourcesTransactionManager.class);

	private static final long serialVersionUID = 4712923770419532385L;

	private IDataSourceService dataSourceService;
	private Map<String, DataSourceTransactionManager> transactionManagers = new HashMap<String, DataSourceTransactionManager>();
	// private ThreadLocal<Map<String, TransactionStatus>> transactionStatusMap
	// = new ThreadLocal<Map<String, TransactionStatus>>();

	private Map<String, AtomicInteger> executeCount = Collections
			.synchronizedMap(new HashMap<String, AtomicInteger>());
	private TransactionManagerAlert transactionAlert; // ���񱨾�
	private TransactionManagerTrigger transactionTrigger;

	public TransactionManagerTrigger getTransactionTrigger() {
		return transactionTrigger;
	}

	public void setTransactionTrigger(TransactionManagerTrigger transactionTrigger) {
		this.transactionTrigger = transactionTrigger;
	}

	public MultiDataSourcesTransactionManager() {
	}

	public void setDataSourceService(IDataSourceService dataSourceService) {
		this.dataSourceService = dataSourceService;
	}

	public IDataSourceService getDataSourceService() {
		return dataSourceService;
	}

	public TransactionManagerAlert getTransactionAlert() {
		return transactionAlert;
	}

	public void setTransactionAlert(TransactionManagerAlert transactionAlert) {
		this.transactionAlert = transactionAlert;
	}

	public void afterPropertiesSet() throws Exception {
		Validate.notNull(dataSourceService);
		executeCount.put("commit", new AtomicInteger(0));
		executeCount.put("rollback", new AtomicInteger(0));
		Map<String, DataSource> map = getDataSourceService().getDataSources();
		for (String dsName : map.keySet()) {
			// this.dataSourceList.add(dsName);
			DataSourceTransactionManager txManager = new DataSourceTransactionManager(
					map.get(dsName));
			transactionManagers.put(dsName, txManager);
		}
		// Collections.sort(this.dataSourceList);
		this.addShutdownHook();
	}

	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				// rollback �� commit���Ϊ0
				while (executeCount.get("commit").get() != 0) {
					logger.info("�ȴ���ݿ��ύ����...");
					try {
						Thread.currentThread().sleep(1);
					} catch (InterruptedException e) {
						logger.warn(
								"interrupted when shuting down the query executor:\n{}",
								e);
					}
				}
				while (executeCount.get("rollback").get() != 0) {
					logger.info("�ȴ���ݿ�ع�����...");
					try {
						Thread.currentThread().sleep(1);
					} catch (InterruptedException e) {
						logger.warn(
								"interrupted when shuting down the query executor:\n{}",
								e);
					}
				}
				logger.info("��ݿ�����������!");
			}
		});

	}

	public TransactionStatus getTransaction(TransactionDefinition definition)
			throws TransactionException {
		MutiDataSourcesTransactionMonitor.addMonitor(this, definition);
		if (this.getDataSourceService().getRouterManager() == null) {
			List<String> needOpenTransDataSources = new ArrayList<String>();
			needOpenTransDataSources.add(this.getDataSourceService()
					.getDefaultIdentity());
			MutiDataSourcesTransactionMonitor
					.openTransactionIfNeed(needOpenTransDataSources);
		}
		return new SimpleTransactionStatus();

	}

	public Map<String, TransactionStatus> openTransaction(
			List<String> needOpenTransDataSources,
			TransactionDefinition definition) {
		// this.transactionDefinitions
		// .set(new HashMap<String, TransactionDefinition>());

		TransactionStatus status = null;
		Map<String, TransactionStatus> statusMap = new HashMap<String, TransactionStatus>();
		for (int i = 0; i < needOpenTransDataSources.size(); i++) {

			String dsName = needOpenTransDataSources.get(i);
			logger.info(dsName + "���ڿ�������...");
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(definition.getPropagationBehavior());
			def.setIsolationLevel(definition.getIsolationLevel());
			def.setName(dsName);
			def.setTimeout(definition.getTimeout());
			def.setReadOnly(definition.isReadOnly());

			PlatformTransactionManager txManager = this.transactionManagers
					.get(dsName);
			status = txManager.getTransaction(def);

			TransactionSynchronizationManager.setCurrentTransactionName(def
					.getName());
			statusMap.put(dsName, status);
		}
		return statusMap;

	}

	public void commit(TransactionStatus status) throws TransactionException {

		Throwable ex = null;
		String dsName = "";
		List<String> dataSourceList = MutiDataSourcesTransactionMonitor
				.getOpenedDataSources();
		for (int i = dataSourceList.size() - 1; i >= 0; i--) {
			try {
				executeCount.get("commit").addAndGet(1);
				dsName = dataSourceList.get(i);
				logger.info(dsName + "�����ύ����...");
				DataSourceTransactionManager txManager = this.transactionManagers
						.get(dsName);
				status = MutiDataSourcesTransactionMonitor
						.getTransactionStatus(dsName);
				txManager.commit(status);
				if (this.getTransactionTrigger() != null) {
					MutiDataSourcesTransactionMonitor.afterDbCommit(dsName,
                            this.getTransactionTrigger(),
                            this.getTransactionAlert());
				}
				logger.info(dsName + "�ύ���� �ɹ�!");
			} catch (Throwable e) {
				MutiDataSourcesTransactionMonitor.saveRequests(dsName,
                        TransactionManagerAlert.AlertType.commit, this.getTransactionAlert());
				logger.info(dsName + "�ύ����ʧ��,ԭ��:" + e.getMessage(), e);
				ex = e;
			} finally {
				executeCount.get("commit").addAndGet(-1);
			}
		}
		MutiDataSourcesTransactionMonitor.closeTransaction();
		if (ex != null) {
			throw new RuntimeException(ex);
		}

	}

	public void rollback(TransactionStatus status) throws TransactionException {
		Throwable ex = null;
		String dsName = "";
		List<String> dataSourceList = MutiDataSourcesTransactionMonitor
				.getOpenedDataSources();
		for (int i = dataSourceList.size() - 1; i >= 0; i--) {
			try {
				executeCount.get("rollback").addAndGet(1);
				dsName = dataSourceList.get(i);
				logger.info(dsName + "���ڻع�����...");
				DataSourceTransactionManager txManager = this.transactionManagers
						.get(dsName);
				status = MutiDataSourcesTransactionMonitor
						.getTransactionStatus(dsName);
				txManager.rollback(status);
				logger.info(dsName + "�ع�����ɹ�!");
			} catch (Throwable e) {
				MutiDataSourcesTransactionMonitor.saveRequests(dsName,
                        TransactionManagerAlert.AlertType.rollback, this.getTransactionAlert());
				logger.info(dsName + "�ع�����ʧ��,ԭ��:" + e.getMessage(), e);
				ex = e;
			} finally {
				executeCount.get("rollback").addAndGet(-1);
			}
		}
		MutiDataSourcesTransactionMonitor.closeTransaction();
		if (ex != null) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void destroy() throws Exception {

	}
}
