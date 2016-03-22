package com.jd.bluedragon.core.dbs.transaction;

import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;

import com.jd.bluedragon.core.dbs.execution.ConcurrentRequest;
import com.jd.bluedragon.core.dbs.transaction.trigger.TransactionManagerTrigger;
import com.jd.bluedragon.core.dbs.transaction.trigger.TriggerMessage;
import com.jd.bluedragon.core.dbs.util.DbsParameter;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;


/**
 * muti datasources transaction monitor,transaction dataSource manager,
 * exception manager
 * 
 * @author admin
 * 
 */
public class MutiDataSourcesTransactionMonitor {
	private static Logger logger = Logger
			.getLogger(MutiDataSourcesTransactionMonitor.class);
	private static ThreadLocal<Stack<MonitorObject>> moStackPool = new ThreadLocal<Stack<MonitorObject>>();
	private static ObjectMapper objectMapper = new ObjectMapper();

	public static synchronized void addMonitor(
			MultiDataSourcesTransactionManager manager,
			TransactionDefinition definition) {
		if (moStackPool.get() == null) {
			Stack<MonitorObject> stack = new Stack<MonitorObject>();
			moStackPool.set(stack);
		}
		MonitorObject mo = new MonitorObject();
		mo.setManager(manager);
		mo.setRequests(new ArrayList<String>());
		mo.setStatusMap(new HashMap<String, TransactionStatus>());
		mo.setDefinition(definition);
		moStackPool.get().push(mo);
	}

	public static MonitorObject getMonitorObject() {
		if (moStackPool.get() == null || moStackPool.get().isEmpty())
			return null;
		MonitorObject mo = moStackPool.get().lastElement();
		return mo;
	}

	public static void openTransactionIfNeed(List<String> dataSourceNames) {
		openTransactionIfNeed(dataSourceNames, null);
	}

	public static void openTransactionIfNeed(List<String> dataSourceNames,
			MonitorObject mo) {
		if (mo == null) {
			mo = getMonitorObject();
		}
		if (mo == null) {
			return;
		}
		MultiDataSourcesTransactionManager transactionManager = mo.getManager();
		if (transactionManager == null)
			return;
		List<String> needOpenTransDataSources = new ArrayList<String>();
		for (String dsName : dataSourceNames) {
			if (!mo.getDataSourceList().contains(dsName)) {
				mo.getDataSourceList().add(dsName);
				needOpenTransDataSources.add(dsName);
			}
		}
		if (needOpenTransDataSources.size() == 0)
			return;
		Map<String, TransactionStatus> statusMap = transactionManager
				.openTransaction(needOpenTransDataSources, mo.getDefinition());
		if (statusMap != null)
			mo.getStatusMap().putAll(statusMap);
	}

	public static TransactionStatus getTransactionStatus(String dataSourceName) {
		MonitorObject mo = getMonitorObject();
		if (mo == null)
			return null;
		MultiDataSourcesTransactionManager transactionManager = mo.getManager();
		if (transactionManager == null)
			return null;
		return mo.getStatusMap().get(dataSourceName);
	}

	public static List<String> getOpenedDataSources() {
		MonitorObject mo = getMonitorObject();
		if (mo == null)
			return new ArrayList<String>();
		return mo.getDataSourceList();
	}

	public static void pushRequest(ConcurrentRequest request) {
		MonitorObject mo = getMonitorObject();
		if (mo == null)
			return;
		RequestManageObject ro = new RequestManageObject();
		ro.setCrud(request.getCrud());
		ro.setDataSourceName(request.getDataSourceName());
		ro.setParameter(request.getParameter());
		ro.setParamClassName(ro.getParameter().getClass().getName());
		ro.setStatement(request.getStatement());
		try {
			String jsonStr = objectMapper.writeValueAsString(ro);
			mo.getRequests().add(jsonStr);
			if (request.getExecutor() != null) {
				if (!mo.getExecutorMap().containsKey(
						request.getDataSourceName())) {
					mo.getExecutorMap().put(request.getDataSourceName(),
							request.getExecutor());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	public static void afterDbCommit(final String dataSourceName,
			final TransactionManagerTrigger transactionTrigger,
			TransactionManagerAlert transactionAlert) {
		if (transactionTrigger == null)
			return;
		try {
			MonitorObject mo = getMonitorObject();
			if (mo == null)
				return;
			List<String> requestList = mo.getRequests();
			final List<TriggerMessage> dbRequests = new ArrayList<TriggerMessage>();
			int sendCount = transactionTrigger.getTriggerMessageCount();
			if (sendCount == 0)
				sendCount = 10;
			List<String> triggerStatements = transactionTrigger
					.getTriggerStatements();
			if (triggerStatements == null || triggerStatements.size() == 0) {
				return;
			}
			for (String requestStr : requestList) {
				RequestManageObject request = objectMapper.readValue(
						requestStr, RequestManageObject.class);
				if (request.getDataSourceName()
						.equalsIgnoreCase(dataSourceName)) {

					boolean isSend = false;
					for (String statement : triggerStatements) {
						if (request.getStatement().equalsIgnoreCase(statement)
								|| request.getStatement().startsWith(statement)) {
							isSend = true;
							break;
						}
					}
					if (!isSend) {
						continue;
					}
					TriggerMessage message = new TriggerMessage();
					message.setStatement(request.getStatement());
					if (request.getParameter() != null
							&& request.getParameter() instanceof DbsParameter) {
						message.setParameter(((DbsParameter) request
								.getParameter()).getParameter());

					} else {
						message.setParameter(request.getParameter());

					}
					message.setParamClassName(request.getParamClassName());
					message.setCrud(request.getCrud());

					dbRequests.add(message);
					if (dbRequests.size() >= sendCount) {
						// sendTriggerMessages(
						// mo.getExecutorMap().get(dataSourceName),
						// transactionTrigger, transactionAlert,
						// dataSourceName, dbRequests);
						sendTriggerMessages(transactionTrigger,
								transactionAlert, dataSourceName, dbRequests);

						dbRequests.clear();
					}
				}
			}
			if (dbRequests.size() > 0) {
				// sendTriggerMessages(mo.getExecutorMap().get(dataSourceName),
				// transactionTrigger, transactionAlert, dataSourceName,
				// dbRequests);
				sendTriggerMessages(transactionTrigger, transactionAlert,
						dataSourceName, dbRequests);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			saveRequests(dataSourceName, TransactionManagerAlert.AlertType.afterCommit,
					transactionAlert);
		}
	}

	private static void sendTriggerMessages(
			TransactionManagerTrigger transactionTrigger,
			TransactionManagerAlert transactionAlert, String dataSourceName,
			List<TriggerMessage> dbRequests) {
		String jsonStr = null;
		try {
			jsonStr = objectMapper.writeValueAsString(dbRequests);
			((TransactionManagerTrigger) transactionTrigger).afterDbCommit(
					dataSourceName, jsonStr);
		} catch (Throwable t) {
			if (transactionAlert != null) {
				transactionAlert.sendRequests(null, jsonStr,
						TransactionManagerAlert.AlertType.afterCommit);
			}
		}
	}

	// private static void sendTriggerMessages(ExecutorService service,
	// final TransactionManagerTrigger transactionTrigger,
	// final TransactionManagerAlert transactionAlert,
	// final String dataSourceName, final List<TriggerMessage> dbRequests)
	// throws Exception {
	// final String jsonStr = objectMapper.writeValueAsString(dbRequests);
	// service.submit(new Callable<Object>() {
	// @Override
	// public Object call() throws Exception {
	// try {
	// ((TransactionManagerTrigger) transactionTrigger)
	// .afterDbCommit(dataSourceName, jsonStr);
	// } catch (Throwable t) {
	// if (transactionAlert != null) {
	// transactionAlert.sendRequests(null, jsonStr,
	// AlertType.afterCommit);
	// }
	// }
	// return null;
	// }
	//
	// });
	// }

	/**
	 * 
	 * @param dataSourceName
	 * @param transactionType
	 *            commit rollback
	 */
	public static void saveRequests(String dataSourceName, TransactionManagerAlert.AlertType alertType,
			TransactionManagerAlert transactionAlert) {
		MonitorObject mo = getMonitorObject();
		if (mo == null)
			return;
		List<String> requestList = mo.getRequests();
		if (requestList.size() > 0) {
			if (transactionAlert != null) {
				transactionAlert.sendMessage(dataSourceName, getLocalIP(),
						alertType);
			}
		}
		List<RequestManageObject> saveList = new ArrayList<RequestManageObject>();
		for (String requestStr : requestList) {
			try {
				RequestManageObject request = objectMapper.readValue(
						requestStr, RequestManageObject.class);
				saveList.add(request);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			if (saveList.size() == 100) {
				writeLogs(dataSourceName, alertType, transactionAlert, saveList);
				saveList.clear();
			}
		}
		if (saveList.size() > 0) {
			writeLogs(dataSourceName, alertType, transactionAlert, saveList);
		}
	}

	private static void writeLogs(String dataSourceName, TransactionManagerAlert.AlertType alertType,
			TransactionManagerAlert transactionAlert,
			List<RequestManageObject> list) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			StringWriter sw = new StringWriter();
			JsonGenerator jsonGenerator = objectMapper.getJsonFactory()
					.createJsonGenerator(sw);
			jsonGenerator.writeObject(list);
			sw.flush();
			String jsonStr = sw.getBuffer().toString();
			sw.close();
			jsonGenerator.close();
			logger.error(dataSourceName + "�ڽ�����ݿ�" + alertType.name()
					+ "ʱ���������쳣,����ش�����쳣,����������ݶ�ʧ!");
			logger.error("------------------------------------------------");
			logger.error(jsonStr);
			logger.error("------------------------------------------------");

			if (transactionAlert != null) {
				transactionAlert.sendRequests(dataSourceName, jsonStr,
						alertType);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void closeTransaction() {
		if (moStackPool.get() == null)
			return;
		moStackPool.get().pop();
	}

	private static String getLocalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}

	public static class MonitorObject {
		private MultiDataSourcesTransactionManager manager;
		private TransactionDefinition definition;
		private List<String> requests;
		private Map<String, TransactionStatus> statusMap;
		private List<String> dataSourceList = new ArrayList<String>();
		private Map<String, ExecutorService> executorMap = new HashMap<String, ExecutorService>();

		public Map<String, ExecutorService> getExecutorMap() {
			return executorMap;
		}

		public List<String> getRequests() {
			return requests;
		}

		public void setRequests(List<String> requests) {
			this.requests = requests;
		}

		public MultiDataSourcesTransactionManager getManager() {
			return manager;
		}

		public void setManager(MultiDataSourcesTransactionManager manager) {
			this.manager = manager;
		}

		public TransactionDefinition getDefinition() {
			return definition;
		}

		public void setDefinition(TransactionDefinition definition) {
			this.definition = definition;
		}

		public Map<String, TransactionStatus> getStatusMap() {
			return statusMap;
		}

		public void setStatusMap(Map<String, TransactionStatus> statusMap) {
			this.statusMap = statusMap;
		}

		public List<String> getDataSourceList() {
			return dataSourceList;
		}

		public void setDataSourceList(List<String> dataSourceList) {
			this.dataSourceList = dataSourceList;
		}
	}
}
