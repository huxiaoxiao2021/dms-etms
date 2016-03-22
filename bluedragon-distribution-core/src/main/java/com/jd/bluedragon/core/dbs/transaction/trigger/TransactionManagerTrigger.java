package com.jd.bluedragon.core.dbs.transaction.trigger;

import java.util.List;

public interface TransactionManagerTrigger {
	int getTriggerMessageCount();

	void setTriggerStatements(List<String> triggerStatements);

	List<String> getTriggerStatements();

	void afterDbCommit(String dataSourceName, String reqeusts) throws Exception;
}
